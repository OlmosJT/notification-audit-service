package uz.tengebank.notificationauditservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.tengebank.notificationauditservice.entity.NotificationRequest;
import uz.tengebank.notificationauditservice.mapper.NotificationMapper;
import uz.tengebank.notificationauditservice.repository.IndividualNotificationRepository;
import uz.tengebank.notificationauditservice.repository.NotificationRequestRepository;
import uz.tengebank.notificationauditservice.repository.NotificationStatusHistoryRepository;
import uz.tengebank.notificationcontracts.events.EventEnvelope;
import uz.tengebank.notificationcontracts.events.EventType;
import uz.tengebank.notificationcontracts.payload.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationAuditService {

    private final NotificationRequestRepository requestRepository;
    private final IndividualNotificationRepository notificationRepository;
    private final NotificationStatusHistoryRepository historyRepository;
    private final NotificationMapper mapper;


    @Transactional
    public void processEvent(EventEnvelope event) {
        switch (event.getEventType()) {
            case EventType.NOTIFICATION_REQUEST_ACCEPTED_V1 ->
                    handleRequestAccepted((NotificationRequestAccepted) event.getPayload(), event.getSourceService());
            case EventType.NOTIFICATION_REQUEST_REJECTED_V1 ->
                    handleRequestRejected((NotificationRequestRejected) event.getPayload(), event.getSourceService())
            default ->
                    log.warn("Received unknown event type: '{}'", event.getEventType());
        }
    }

    private void handleRequestRejected(NotificationRequestRejected payload, String sourceService) {
        var requestOpt = requestRepository.findByRequestId(payload.requestId());
        if(requestOpt.isPresent()) {

        }
    }

    /*
    public record NotificationRequestRejected(
        UUID requestId,
        String source,
        String reason,
        Map<String, String> details
) implements Payload {
}
     */


    private void handleRequestAccepted(NotificationRequestAccepted payload, String sourceService) {
        NotificationRequest notificationRequest = mapper.toNotificationRequestEntity(payload);
        if (requestRepository.findByRequestId(notificationRequest.requestId()).isEmpty()) {
            requestRepository.save(notificationRequest);
        }
    }

}
