package uz.tengebank.notificationauditservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.tengebank.notificationauditservice.entity.IndividualNotification;
import uz.tengebank.notificationauditservice.entity.NotificationRequest;
import uz.tengebank.notificationcontracts.payload.NotificationRequestAccepted;

import java.io.UncheckedIOException;
import java.time.OffsetDateTime;

/**
 * Maps event payloads to JPA entities.
 */
@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final ObjectMapper objectMapper;

    private String serializePayloadToJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException("Failed to serialize payload to JSON for auditing", e);
        }
    }

    /**
     * Maps the NotificationRequestAccepted event payload to a NotificationRequest JPA entity.
     * It correctly serializes the full payload into a JSON string for auditing.
     *
     * @param payload The incoming event payload.
     * @return A fully populated NotificationRequest entity, ready to be saved.
     */
    public NotificationRequest toNotificationRequestEntity(NotificationRequestAccepted payload) {
        var request = new NotificationRequest();
        request.requestId(payload.requestId());
        request.templateName(payload.templateName());
        request.source(payload.source());
        request.category(payload.category());
        request.fullRequestPayload(serializePayloadToJson(payload.fullRequestPayloadAsJson()));
        request.receivedAt(OffsetDateTime.now());
        return request;
    }



    public NotificationStatusHistory toHistoryEntity(
            IndividualNotification notification,
            NotificationStatus status,
            String sourceService,
            String details,
            String reasonCode,
            String providerMessageId) {

        var history = new NotificationStatusHistory();
        history.individualNotification(notification);
        history.status(status);
        history.sourceService(sourceService);
        history.details(details);
        history.reasonCode(reasonCode);
        history.providerMessageId(providerMessageId);
        history.occurredAt(OffsetDateTime.now());
        return history;
    }
}
