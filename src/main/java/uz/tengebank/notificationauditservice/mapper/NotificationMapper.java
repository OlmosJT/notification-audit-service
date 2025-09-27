package uz.tengebank.notificationauditservice.mapper;

import org.springframework.stereotype.Component;
import uz.tengebank.notificationauditservice.entity.IndividualNotification;
import uz.tengebank.notificationauditservice.entity.NotificationRequest;
import uz.tengebank.notificationauditservice.entity.NotificationStatusHistory;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;
import uz.tengebank.notificationcontracts.events.enums.NotificationStatus;
import uz.tengebank.notificationcontracts.payload.NotificationRequestAccepted;

import java.time.OffsetDateTime;

/**
 * Maps event payloads to JPA entities.
 */
@Component
public class NotificationMapper {

    public NotificationRequest toNotificationRequestEntity(NotificationRequestAccepted payload) {
        var request = new NotificationRequest();
        request.requestId(payload.requestId());
        request.source(payload.source());
        request.category(payload.category());
        request.templateName(payload.templateName());
        // For simplicity, serializing the whole payload. In a real scenario, you might use a library.
        request.fullRequestPayload(payload.toString()); // TODO: Consider using a library.
        request.receivedAt(OffsetDateTime.now());
        return request;
    }

    public IndividualNotification toIndividualNotificationEntity(
            NotificationRequest request,
            NotificationRequestAccepted.Recipient recipientPayload,
            NotificationStatus initialStatus
    ) {

        var notification = new IndividualNotification();
        notification.notificationRequest(request);
        notification.recipientId(recipientPayload.id());
        // Assuming SMS is the primary channel for now. A more complex system might have better logic.
        notification.channelType(ChannelType.SMS); // TODO: Consider usage here.
        notification.destinationAddress(recipientPayload.phone());
        notification.currentStatus(initialStatus);
        notification.createdAt(OffsetDateTime.now());
        notification.updatedAt(OffsetDateTime.now());
        return notification;
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
