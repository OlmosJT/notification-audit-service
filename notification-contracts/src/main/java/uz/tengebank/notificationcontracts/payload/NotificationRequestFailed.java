package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.dto.FailureDetails;

import java.util.UUID;

/**
 * Emitted when a request fails catastrophically during processing.
 */
public record NotificationRequestFailed (
        UUID requestId,
        FailureDetails reason
) implements Payload {
}
