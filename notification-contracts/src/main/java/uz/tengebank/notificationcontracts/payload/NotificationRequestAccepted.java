package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.dto.NotificationRequestDto;

/**
 * Emitted when a notification request is accepted for processing.
 * The payload contains the full, original request for auditing.
 */
public record NotificationRequestAccepted (
        NotificationRequestDto originalRequest
) implements Payload {
}
