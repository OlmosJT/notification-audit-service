package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.dto.NotificationRequest;
import uz.tengebank.notificationcontracts.dto.enums.NotificationRequestStatus;

import java.util.UUID;

public record NotificationRequestPayload(
        UUID requestId,
        NotificationRequestStatus status,
        String message,
        String failureReason,
        NotificationRequest requestDetails
) implements Payload {
    @Override
    public UUID getReferenceId() {
        return requestId;
    }
}
