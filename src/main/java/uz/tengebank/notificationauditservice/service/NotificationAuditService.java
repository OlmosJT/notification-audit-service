package uz.tengebank.notificationauditservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.tengebank.notificationauditservice.entity.NotificationAuditDestinationEntity;
import uz.tengebank.notificationauditservice.entity.NotificationAuditRequestEntity;
import uz.tengebank.notificationauditservice.entity.NotificationDestinationHistoryEntity;
import uz.tengebank.notificationauditservice.entity.NotificationRequestHistoryEntity;
import uz.tengebank.notificationauditservice.repository.NotificationAuditDestinationRepository;
import uz.tengebank.notificationauditservice.repository.NotificationAuditRequestRepository;
import uz.tengebank.notificationauditservice.repository.NotificationDestinationHistoryRepository;
import uz.tengebank.notificationauditservice.repository.NotificationRequestHistoryRepository;
import uz.tengebank.notificationcontracts.dto.NotificationRequest;
import uz.tengebank.notificationcontracts.dto.enums.IndividualNotificationStatus;
import uz.tengebank.notificationcontracts.events.EventEnvelope;
import uz.tengebank.notificationcontracts.events.EventType;
import uz.tengebank.notificationcontracts.payload.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAuditService {

    // Repositories for Parent (Static) Entities - READ ONLY LOOKUPS
    private final NotificationAuditRequestRepository requestRepository;
    private final NotificationAuditDestinationRepository destinationRepository;

    // Repositories for History Entities - INSERT ONLY
    private final NotificationRequestHistoryRepository requestHistoryRepo;
    private final NotificationDestinationHistoryRepository destinationHistoryRepo;


    @Transactional
    public void processEvent(EventEnvelope envelope) {
        Payload payload = envelope.getPayload();

        if (payload instanceof NotificationRequestPayload requestPayload) {
            handleRequestEvent(envelope, requestPayload);
        }
        else if (payload instanceof NotificationDestinationPayload destinationPayload) {
            handleDestinationEvent(envelope, destinationPayload);
        }
        else {
            log.warn("Received unknown payload type: {}", payload.getClass().getSimpleName());
        }
    }


    private void handleRequestEvent(EventEnvelope envelope, NotificationRequestPayload payload) {
        switch (envelope.getEventType()) {
            case EventType.NOTIFICATION_REQUEST_ACCEPTED_V1 -> createInitialEntities(envelope, payload);
            default -> updateRequestHistory(envelope, payload);
        }
    }

    private void createInitialEntities(EventEnvelope envelope, NotificationRequestPayload payload) {
        NotificationRequest dto = payload.requestDetails();
        NotificationRequest.Metadata metadata = dto.metadata();

        log.info("Creating new Audit Entities for Request: {}", dto.requestId());

        // Parent
        var requestEntity = new NotificationAuditRequestEntity();

        requestEntity.setRequestId(dto.requestId());
        requestEntity.setChannel(dto.channel());
        requestEntity.setTemplateCode(dto.templateCode());
        requestEntity.setLanguage(dto.language());
        requestEntity.setSourceSystem(metadata.source());
        requestEntity.setPriority(metadata.priority());
        requestEntity.setBatch(metadata.batch());
        requestEntity.setScheduleAt(metadata.scheduleAt());
        requestEntity.setTtlSeconds(metadata.ttlSeconds());
        requestEntity.setCallbackUrl(metadata.callbackUrl());
        requestEntity.setRetryMaxRetries(metadata.retryPolicy().maxRetries());
        requestEntity.setRetryDelaySeconds(metadata.retryPolicy().retryDelaySeconds());
        requestEntity.setTags(metadata.tags());
        requestEntity.setDestinations(new ArrayList<>());

        // Child Destinations
        List<NotificationAuditDestinationEntity> destEntities = dto.destinations().stream().map(destDto -> {
            var entity = new NotificationAuditDestinationEntity();
            entity.setRequest(requestEntity);
            entity.setDestinationId(destDto.destinationId());
            entity.setPhone(destDto.phone());
            entity.setEmail(destDto.email());
            entity.setPushToken(destDto.pushToken());
            entity.setTemplateVariables(destDto.variables());
            return entity;
        }).toList();

        requestEntity.getDestinations().addAll(destEntities);

        requestRepository.save(requestEntity);

        // Track the request history
        var reqHistory = NotificationRequestHistoryEntity.builder()
                .request(requestEntity)
                .reporterService(envelope.getSourceService())
                .eventType(envelope.getEventType())
                .status(payload.status())
                .details(payload.message())
                .occurredAt(envelope.getEventTimestamp())
                .build();
        requestHistoryRepo.save(reqHistory);

        List<NotificationDestinationHistoryEntity> destHistories = destEntities.stream().map(destEntity ->
                NotificationDestinationHistoryEntity.builder()
                        .destination(destEntity)
                        .status(IndividualNotificationStatus.ACCEPTED)
                        .reporterService(envelope.getSourceService())
                        .eventType(envelope.getEventType())
                        .occurredAt(envelope.getEventTimestamp())
                        .build()
        ).toList();

        destinationHistoryRepo.saveAll(destHistories);
    }

    private void updateRequestHistory(EventEnvelope envelope, NotificationRequestPayload payload) {
        var parentRequest = requestRepository.findById(payload.requestId())
                .orElseThrow(() -> new RuntimeException("Parent Request not found: " + payload.requestId()));

        String details = payload.message();
        if (payload.failureReason() != null && !payload.failureReason().isBlank()) {
            details = (details != null ? details + " | " : "") + payload.failureReason();
        }

        var history = NotificationRequestHistoryEntity.builder()
                .request(parentRequest)
                .reporterService(envelope.getSourceService())
                .eventType(envelope.getEventType())
                .status(payload.status())
                .details(details)
                .occurredAt(envelope.getEventTimestamp())
                .build();

        requestHistoryRepo.save(history);
        log.info("Updated Request History: {} -> {}", payload.requestId(), payload.status());
    }

    private void handleDestinationEvent(EventEnvelope envelope, NotificationDestinationPayload payload) {
        var parentDest = destinationRepository.findByRequestRequestIdAndDestinationId(payload.requestId(), payload.destinationId())
                .orElseThrow(() -> new RuntimeException(
                        "Parent Destination not found for requestId=" + payload.requestId()
                                + " and destinationId=" + payload.destinationId()
                ));

        var history = NotificationDestinationHistoryEntity.builder()
                .destination(parentDest)
                .eventType(envelope.getEventType())
                .reporterService(envelope.getSourceService())
                .status(payload.status())
                .errorMessage(payload.errorCode())
                .providerRawResponse(payload.channelResponse())
                .occurredAt(envelope.getEventTimestamp())
                .build();

        destinationHistoryRepo.save(history);
        log.info("Updated Destination History: requestId={} destinationId={} -> {}",
                payload.requestId(), payload.destinationId(), payload.status());
    }
}
