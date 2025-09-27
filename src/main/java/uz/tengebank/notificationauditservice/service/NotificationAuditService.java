package uz.tengebank.notificationauditservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.tengebank.notificationauditservice.entity.IndividualNotification;
import uz.tengebank.notificationauditservice.entity.NotificationRequest;
import uz.tengebank.notificationauditservice.mapper.NotificationMapper;
import uz.tengebank.notificationauditservice.repository.IndividualNotificationRepository;
import uz.tengebank.notificationauditservice.repository.NotificationRequestRepository;
import uz.tengebank.notificationauditservice.repository.NotificationStatusHistoryRepository;
import uz.tengebank.notificationcontracts.events.EventEnvelope;
import uz.tengebank.notificationcontracts.events.EventType;
import uz.tengebank.notificationcontracts.events.enums.NotificationStatus;
import uz.tengebank.notificationcontracts.payload.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAuditService {

    private final NotificationRequestRepository requestRepository;
    private final IndividualNotificationRepository notificationRepository;
    private final NotificationStatusHistoryRepository historyRepository;
    private final NotificationMapper mapper;

    /**
     * Main entry point for processing all notification events.
     * Routes the event to the appropriate handler based on its type.
     *
     * @param event The event envelope from the message queue.
     */
    public void processEvent(EventEnvelope event) {
        // This switch acts as a router to the appropriate transactional business logic.
        switch (event.getEventType()) {
            case EventType.NOTIFICATION_REQUEST_ACCEPTED_V1 ->
                    handleRequestAccepted((NotificationRequestAccepted) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_REQUEST_REJECTED_V1 ->
                    handleRequestRejected((NotificationRequestRejected) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_CHANNEL_ROUTED_V1 ->
                    handleChannelRouted((NotificationChannelRouted) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_PROCESSING_STARTED_V1 ->
                    handleProcessingStarted((NotificationProcessingStarted) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_PROVIDER_ACCEPTED_V1 ->
                    handleProviderAccepted((NotificationProviderAccepted) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_PROVIDER_REJECTED_V1 ->
                    handleProviderRejected((NotificationProviderRejected) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_ATTEMPT_FAILED_V1 ->
                    handleAttemptFailed((NotificationAttemptFailed) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_DELIVERED_V1 ->
                    handleDelivered((NotificationDelivered) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_UNDELIVERED_V1 ->
                    handleUndelivered((NotificationUndelivered) event.getPayload(), event.getSourceService());
            default ->
                    log.warn("Received unknown event type: '{}'", event.getEventType());
        }
    }

    @Transactional
    public void handleRequestAccepted(NotificationRequestAccepted payload, String sourceService) {
        NotificationRequest request = mapper.toNotificationRequestEntity(payload);
        requestRepository.save(request);

        for (var recipientPayload : payload.recipients()) {
            IndividualNotification notification = mapper.toIndividualNotificationEntity(request, recipientPayload, NotificationStatus.REQUEST_ACCEPTED);
            notificationRepository.save(notification);
            createHistory(notification, NotificationStatus.REQUEST_ACCEPTED, sourceService, null, null, null);
        }
    }

    @Transactional
    public void handleRequestRejected(NotificationRequestRejected payload, String sourceService) {
        // For rejected requests, we log the attempt by creating a NotificationRequest,
        // but no IndividualNotification records are created as they were never processed.
        // A UI can later find requests that have no associated notifications to see these failures.
        var request = new NotificationRequest();
        request.requestId(payload.requestId());
        request.source(payload.source());
        request.category("N/A"); // Category might not be available in a rejected payload
        request.templateName("N/A");
        request.fullRequestPayload(payload.toString());
        request.receivedAt(OffsetDateTime.now());
        requestRepository.save(request);
        log.info("Logged rejected request with ID: {}", payload.requestId());
    }

    @Transactional
    public void handleChannelRouted(NotificationChannelRouted payload, String sourceService) {
        updateStatus(payload.requestId(), payload.recipientId(), NotificationStatus.CHANNEL_ROUTED, sourceService, payload.toString(), null, null);
    }

    @Transactional
    public void handleProcessingStarted(NotificationProcessingStarted payload, String sourceService) {
        updateStatus(payload.requestId(), payload.recipientId(), NotificationStatus.PROCESSING_STARTED, sourceService, payload.toString(), null, null);
    }

    @Transactional
    public void handleProviderAccepted(NotificationProviderAccepted payload, String sourceService) {
        IndividualNotification notification = findNotification(payload.requestId(), payload.recipientId());
        notification.provider(payload.providerName());
        notification.providerMessageId(payload.providerMessageId());
        updateStatus(notification, NotificationStatus.PROVIDER_ACCEPTED, sourceService, payload.toString(), null, payload.providerMessageId());
    }

    @Transactional
    public void handleProviderRejected(NotificationProviderRejected payload, String sourceService) {
        IndividualNotification notification = findNotification(payload.requestId(), payload.recipientId());
        notification.provider(payload.providerName());
        updateStatus(notification, NotificationStatus.PROVIDER_REJECTED, sourceService, payload.details(), payload.reason(), null);
    }

    @Transactional
    public void handleAttemptFailed(NotificationAttemptFailed payload, String sourceService) {
        updateStatus(payload.requestId(), payload.recipientId(), NotificationStatus.ATTEMPT_FAILED, sourceService, payload.details(), payload.reason(), null);
    }

    @Transactional
    public void handleDelivered(NotificationDelivered payload, String sourceService) {
        IndividualNotification notification = findNotificationByProviderId(payload.providerMessageId());
        updateStatus(notification, NotificationStatus.DELIVERED, sourceService, payload.toString(), null, payload.providerMessageId());
    }

    @Transactional
    public void handleUndelivered(NotificationUndelivered payload, String sourceService) {
        IndividualNotification notification = findNotificationByProviderId(payload.providerMessageId());
        updateStatus(notification, NotificationStatus.UNDELIVERED, sourceService, null, payload.reason(), payload.providerMessageId());
    }

    // --- HELPER METHODS ---

    private void updateStatus(UUID requestId, UUID recipientId, NotificationStatus status, String sourceService, String details, String reason, String providerMessageId) {
        IndividualNotification notification = findNotification(requestId, recipientId);
        updateStatus(notification, status, sourceService, details, reason, providerMessageId);
    }

    private void updateStatus(IndividualNotification notification, NotificationStatus status, String sourceService, String details, String reason, String providerMessageId) {
        notification.currentStatus(status);
        notification.updatedAt(OffsetDateTime.now());
        notificationRepository.save(notification);
        createHistory(notification, status, sourceService, details, reason, providerMessageId);
    }

    private void createHistory(IndividualNotification notification, NotificationStatus status, String sourceService, String details, String reason, String providerMessageId) {
        var history = mapper.toHistoryEntity(notification, status, sourceService, details, reason, providerMessageId);
        historyRepository.save(history);
    }

    private IndividualNotification findNotification(UUID requestId, UUID recipientId) {
        return notificationRepository
                .findByNotificationRequestRequestIdAndRecipientId(requestId, recipientId.toString())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found for requestId=" + requestId + " and recipientId=" + recipientId
                ));
    }

    private IndividualNotification findNotificationByProviderId(String providerMessageId) {
        return notificationRepository
                .findByProviderMessageId(providerMessageId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification not found for providerMessageId=" + providerMessageId
                ));
    }
}
