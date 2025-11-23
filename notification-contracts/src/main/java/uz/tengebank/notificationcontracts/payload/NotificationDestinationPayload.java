package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.dto.enums.IndividualNotificationStatus;

import java.util.UUID;

public record NotificationDestinationPayload(
        UUID requestId,
        UUID destinationId,
        IndividualNotificationStatus status,
        String channelResponse,
        String errorCode
) implements Payload {
    @Override
    public UUID getReferenceId() {
        return destinationId;
    }
}
