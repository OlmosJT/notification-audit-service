package uz.tengebank.notificationauditservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.tengebank.notificationauditservice.entity.IndividualNotificationEntity;
import uz.tengebank.notificationauditservice.entity.IndividualNotificationStatusHistory;
import uz.tengebank.notificationauditservice.entity.NotificationRequestEntity;
import uz.tengebank.notificationauditservice.entity.NotificationRequestStatusHistory;
import uz.tengebank.notificationauditservice.mapper.NotificationMapper;
import uz.tengebank.notificationauditservice.repository.IndividualNotificationRepository;
import uz.tengebank.notificationauditservice.repository.NotificationRequestRepository;
import uz.tengebank.notificationauditservice.repository.NotificationStatusHistoryRepository;
import uz.tengebank.notificationcontracts.dto.NotificationRequestDto;
import uz.tengebank.notificationcontracts.events.EventEnvelope;
import uz.tengebank.notificationcontracts.events.EventType;
import uz.tengebank.notificationcontracts.events.enums.*;
import uz.tengebank.notificationcontracts.payload.*;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAuditService {

    private final NotificationRequestRepository requestRepository;
    private final IndividualNotificationRepository notificationRepository;
    private final NotificationStatusHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public void processEvent(EventEnvelope event) {
        switch (event.getEventType()) {
            case EventType.NOTIFICATION_REQUEST_ACCEPTED_V1 ->
                    handleRequestAccepted((NotificationRequestAccepted) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_REQUEST_REJECTED_V1 ->
                    handleRequestRejected((NotificationRequestRejected) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_REQUEST_PROCESSING_V1 ->
                    handleRequestProcessing((NotificationRequestProcessing) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_REQUEST_FAILED_V1 ->
                    handleRequestFailed((NotificationRequestFailed) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_REQUEST_COMPLETED_V1, EventType.NOTIFICATION_REQUEST_PARTIALLY_COMPLETED_V1 ->
                    handleRequestCompleted((NotificationRequestCompleted) event.getPayload(), event.getSourceService());

            case EventType.INDIVIDUAL_NOTIFICATION_ACCEPTED_V1 ->
                handleIndividualRequestAccepted((IndividualNotificationAccepted) event.getPayload(), event.getSourceService());
            default ->
                    log.warn("Received unknown event type: '{}'", event.getEventType());
        }
    }

    /**
     * Handles the event indicating a notification request has completed.
     * This method verifies the status of all individual notifications to determine
     * if the final status is COMPLETED or PARTIALLY_COMPLETED.
     *
     * @param payload       The event payload containing the requestId and completion time.
     * @param sourceService The service that published the event.
     */
    private void handleRequestCompleted(NotificationRequestCompleted payload, String sourceService) {
        UUID requestId = payload.requestId();

        // 1. Find the existing request. It MUST exist.
        var requestEntity = requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalStateException(
                        "Received COMPLETED event for an unknown requestId: " + requestId
                ));

        // 2. Idempotency Check: Ignore if already in a terminal state.
        var latestStatus = requestEntity.statusHistory().stream()
                .max(Comparator.comparing(NotificationRequestStatusHistory::occurredAt))
                .map(NotificationRequestStatusHistory::status)
                .orElse(null);

        if (isTerminal(latestStatus)) {
            log.warn("Ignoring COMPLETED event for requestId='{}': request is already in a terminal state ('{}').", requestId, latestStatus);
            return;
        }

        // 3. Verify the final status by checking all individual notifications.
        long successCount = 0;
        long failureCount = 0;
        long totalIndividuals = requestEntity.individualNotifications().size();

        for (var individual : requestEntity.individualNotifications()) {
            if (individual.currentStatus() == IndividualNotificationStatus.DELIVERED) {
                successCount++;
            } else if (isFailed(individual.currentStatus())) {
                failureCount++;
            }
        }

        // Determine the correct final status based on verification.
        final NotificationRequestStatus finalStatus;
        final String summary;

        // Handle BROADCAST requests or cases with no individuals separately.
        if (totalIndividuals == 0) {
            finalStatus = NotificationRequestStatus.COMPLETED;
            summary = "Request marked as completed with no individual notifications to verify.";
        } else if (failureCount > 0) {
            finalStatus = NotificationRequestStatus.PARTIALLY_COMPLETED;
            summary = String.format("Request partially completed with %d successes and %d failures.", successCount, failureCount);
        } else {
            finalStatus = NotificationRequestStatus.COMPLETED;
            summary = String.format("Request completed successfully with %d notifications.", successCount);
        }

        if (successCount + failureCount < totalIndividuals) {
            log.warn("COMPLETED event for requestId='{}' received, but {} individual notifications are not yet in a terminal state.",
                    requestId, totalIndividuals - (successCount + failureCount));
        }

        // 4. Create the final status history record for the parent request.
        var requestHistory = new NotificationRequestStatusHistory();
        requestHistory.request(requestEntity) // Link to parent
                .status(finalStatus)
                .sourceService(sourceService)
                .details(toJson(Map.of("summary", summary, "successCount", successCount, "failureCount", failureCount)))
                .occurredAt(payload.completedAt());

        requestEntity.statusHistory().add(requestHistory);

        // 5. Save all changes.
        requestRepository.save(requestEntity);
        log.info("Updated status to {} for notification request with requestId='{}'", finalStatus, requestId);
    }

    /**
     * Handles a catastrophic failure of an entire notification request during processing.
     * This marks the parent request as FAILED and cascades the failure to all
     * non-terminal individual notifications.
     *
     * @param payload       The event payload containing the requestId and failure reason.
     * @param sourceService The service that published the event.
     */
    private void handleRequestFailed(NotificationRequestFailed payload, String sourceService) {
        UUID requestId = payload.requestId();

        // 1. Find the existing request. It MUST exist.
        var requestEntity = requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalStateException(
                        "Received FAILED event for an unknown requestId: " + requestId
                ));

        // 2. Idempotency Check: Get the latest status to prevent invalid transitions.
        var latestStatus = requestEntity.statusHistory().stream()
                .max(Comparator.comparing(NotificationRequestStatusHistory::occurredAt))
                .map(NotificationRequestStatusHistory::status)
                .orElse(null);

        if (latestStatus == NotificationRequestStatus.FAILED ||
                latestStatus == NotificationRequestStatus.COMPLETED ||
                latestStatus == NotificationRequestStatus.REJECTED
        ) {
            log.warn("Ignoring FAILED event for requestId='{}': request is already in a terminal state ('{}').", requestId, latestStatus);
            return;
        }

        // 3. Cascade the failure to all non-terminal individual notifications.
        for (var individualEntity : requestEntity.individualNotifications()) {
            // Only update if it's not already in a final state (e.g., DELIVERED, FAILED)
            if (!isTerminal(individualEntity.currentStatus())) {
                individualEntity.currentStatus(IndividualNotificationStatus.INTERNAL_FAILURE);
                individualEntity.updatedAt(OffsetDateTime.now());

                // Add a corresponding history record for the individual failure
                var individualHistory = new IndividualNotificationStatusHistory();
                individualHistory.individualNotification(individualEntity)
                        .status(IndividualNotificationStatus.INTERNAL_FAILURE)
                        .sourceService(sourceService)
                        .details(toJson(payload.reason()))
                        .occurredAt(OffsetDateTime.now());
                individualEntity.statusHistory().add(individualHistory);
            }
        }

        // 4. Create the final FAILED status history record for the parent request.
        var requestHistory = new NotificationRequestStatusHistory();
        requestHistory.request(requestEntity) // Link to parent
                .status(NotificationRequestStatus.FAILED)
                .sourceService(sourceService)
                .details(toJson(payload.reason()))
                .occurredAt(OffsetDateTime.now());

        requestEntity.statusHistory().add(requestHistory);

        // 5. Save all changes. JPA will update the parent, children, and add all new history records.
        requestRepository.save(requestEntity);
        log.info("Updated status to FAILED for notification request with requestId='{}'", requestId);
    }

    /**
     * Handles the event indicating that a notification request has entered the processing phase.
     *
     * @param payload       The event payload containing the requestId.
     * @param sourceService The service that published the event.
     */
    private void handleRequestProcessing(NotificationRequestProcessing payload, String sourceService) {
        UUID requestId = payload.requestId();

        // 1. Find the existing request. It MUST exist at this stage.
        var requestEntity = requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalStateException(
                        "Received PROCESSING event for an unknown requestId: " + requestId
                ));

        // 2. Idempotency Check: Get the latest status to prevent invalid transitions.
        var latestStatus = requestEntity.statusHistory().stream()
                .max(Comparator.comparing(NotificationRequestStatusHistory::occurredAt))
                .map(NotificationRequestStatusHistory::status)
                .orElse(null);

        if (latestStatus == NotificationRequestStatus.PROCESSING || latestStatus == NotificationRequestStatus.COMPLETED ||
                latestStatus == NotificationRequestStatus.FAILED || latestStatus == NotificationRequestStatus.REJECTED) {
            log.warn("Ignoring PROCESSING event for requestId='{}': current status is '{}'.", requestId, latestStatus);
            return;
        }

        // 3. Create the new status history record
        var requestHistory = new NotificationRequestStatusHistory();
        requestHistory.request(requestEntity) // Link to parent
                .status(NotificationRequestStatus.PROCESSING)
                .sourceService(sourceService)
                .details(toJson(Map.of("info", "Request processing has started.")))
                .occurredAt(OffsetDateTime.now());

        requestEntity.statusHistory().add(requestHistory);

        // 4. Save the updated entity with its new history record.
        requestRepository.save(requestEntity);
        log.info("Updated status to PROCESSING for notification request with requestId='{}'", requestId);
    }

    /**
     * Handles a request rejected pre-processing (e.g., due to validation, templating validation errors).
     * It finds the existing request or creates a minimal one and logs the rejection.
     *
     * @param payload       The event payload containing the requestId and reason.
     * @param sourceService The service that published the event.
     */
    private void handleRequestRejected(NotificationRequestRejected payload, String sourceService) {
        UUID requestId = payload.requestId();

        var requestEntity = requestRepository.findByRequestId(requestId)
                .orElseGet(() -> createMinimalRejectedRequest(requestId, payload));

        // Idempotency Check: Don't add a 'REJECTED' status if it's already the final state.
        if (!requestEntity.statusHistory().isEmpty()) {
            var latestStatus = requestEntity.statusHistory().getLast().status();
            if (latestStatus == NotificationRequestStatus.REJECTED) {
                log.warn("Request with requestId='{}' has already been rejected. Ignoring duplicate event.", requestId);
                return;
            }
        }

        // Create the new status history record for the rejection
        var requestHistory = new NotificationRequestStatusHistory();
        requestHistory.request(requestEntity)
                .status(NotificationRequestStatus.REJECTED)
                .sourceService(sourceService)
                .details(toJson(payload.reason()))
                .occurredAt(OffsetDateTime.now());

        requestEntity.statusHistory().add(requestHistory);

        requestRepository.save(requestEntity);
        log.info("Successfully processed rejection for notification request with requestId='{}'", requestId);
    }

    /**
     * Handles the creation of the initial audit records for an accepted notification request.
     * This method is idempotent and will not create duplicates for the same requestId.
     *
     * @param payload The event payload containing the original request DTO.
     * @param sourceService The service that published the event.
     */
    private void handleRequestAccepted(NotificationRequestAccepted payload, String sourceService) {
        NotificationRequestDto requestDto = payload.originalRequest();
        UUID requestId = requestDto.requestId();

        if (requestRepository.findByRequestId(requestId).isPresent()) {
            log.warn("Request with requestId='{}' has already been accepted. Ignoring duplicate event.", requestId);
            return;
        }

        // 1. Create and populate the main NotificationRequestEntity
        var requestEntity = new NotificationRequestEntity();
        requestEntity
                .requestId(requestDto.requestId())
                .source(requestDto.source())
                .category(requestDto.category())
                .templateName(requestDto.templateName())
                .audienceStrategy(requestDto.audienceStrategy())
                .deliveryStrategy(requestDto.deliveryStrategy())
                .channels(requestDto.channels())
                .channelParams(toJson(requestDto.channelParams()))
                .fullRequestPayload(toJson(requestDto))
                .receivedAt(OffsetDateTime.now());

        // 2. Create the initial status history for the parent request
        var requestHistory = new NotificationRequestStatusHistory();
        requestHistory.request(requestEntity) // Link to parent
                .status(NotificationRequestStatus.ACCEPTED)
                .sourceService(sourceService)
                .details(toJson(Map.of("info", "Request accepted for processing.")))
                .occurredAt(OffsetDateTime.now());

        requestEntity.statusHistory().add(requestHistory);

        // 3. Conditionally create records for each recipient
        if (requestDto.audienceStrategy() == AudienceStrategy.DIRECT || requestDto.audienceStrategy() == AudienceStrategy.GROUP) {

            for (NotificationRequestDto.Recipient recipientDto : requestDto.recipients()) {
                var individualEntity = new IndividualNotificationEntity();
                individualEntity.request(requestEntity)
                        .recipientId(recipientDto.id())
                        .currentStatus(IndividualNotificationStatus.ACCEPTED)
                        .createdAt(OffsetDateTime.now())
                        .updatedAt(OffsetDateTime.now());

                var individualHistory = new IndividualNotificationStatusHistory();
                individualHistory.individualNotification(individualEntity)
                        .status(IndividualNotificationStatus.ACCEPTED)
                        .sourceService(sourceService)
                        .details(toJson(Map.of("info", "Individual notification created.")))
                        .occurredAt(OffsetDateTime.now());

                individualEntity.statusHistory().add(individualHistory);

                requestEntity.individualNotifications().add(individualEntity);
            }
        }

        // 4. Save the parent entity. JPA's cascading will save all children.
        requestRepository.save(requestEntity);
        log.info("Successfully processed and stored new notification request with requestId='{}'", requestId);
    }


    /**
     * Creates a minimal, placeholder NotificationRequestEntity when a rejection is the
     * first event received for a given requestId.
     * * NOTE: In a real-world scenario, it's often better for the publisher to first emit
     * an ACCEPTED event with the full DTO, followed by a REJECTED event. This avoids
     * creating placeholder data in the audit service.
     */
    private NotificationRequestEntity createMinimalRejectedRequest(UUID requestId, NotificationRequestRejected payload) {
        log.warn("Creating minimal request for rejected requestId='{}'. Full request data was not available.", requestId);
        var entity = new NotificationRequestEntity();
        entity.requestId(requestId)
                .source("UNKNOWN") // Placeholder
                .category("UNKNOWN") // Placeholder
                .templateName("UNKNOWN") // Placeholder
                .audienceStrategy(AudienceStrategy.DIRECT) // Placeholder
                .deliveryStrategy(DeliveryStrategy.SINGLE) // Placeholder
                .channels(EnumSet.noneOf(ChannelType.class)) // Placeholder
                .fullRequestPayload(toJson(payload)) // Store the rejection payload for context
                .receivedAt(OffsetDateTime.now());
        return entity;
    }


    /**
     * Helper method to serialize an object to a JSON string.
     */
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error serializing object to JSON", e);
            return "{\"error\":\"Failed to serialize object\"}";
        }
    }

    /**
     * Helper method to check if an individual notification status is terminal.
     */
    private boolean isTerminal(IndividualNotificationStatus status) {
        return status == IndividualNotificationStatus.DELIVERED ||
                status == IndividualNotificationStatus.DELIVERY_FAILED ||
                status == IndividualNotificationStatus.INTERNAL_FAILURE;
    }

    private boolean isTerminal(NotificationRequestStatus status) {
        return status == NotificationRequestStatus.COMPLETED ||
                status == NotificationRequestStatus.PARTIALLY_COMPLETED ||
                status == NotificationRequestStatus.FAILED ||
                status == NotificationRequestStatus.REJECTED;
    }

    private boolean isFailed(IndividualNotificationStatus status) {
        return status == IndividualNotificationStatus.DELIVERY_FAILED ||
                status == IndividualNotificationStatus.INTERNAL_FAILURE;
    }
}
