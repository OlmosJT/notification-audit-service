package uz.tengebank.notificationcontracts.payload;

import java.util.UUID;

/**
 * Event published when an attempt to notify a single recipient over a specific
 * channel fails. The overall batch processing may continue for other recipients.
 *
 * @param requestId   The unique UUID of the original batch request.
 * @param recipientId The identifier of the recipient who was not notified (e.g., phone number).
 * @param channel     The channel (e.g., "PUSH", "SMS") on which the attempt failed.
 * @param reason      A machine-readable code for the failure (e.g., "TOKEN_NOT_FOUND", "RENDER_FAILED").
 * @param details     A human-readable description of the error for diagnostics.
 */
public record NotificationAttemptFailed(
    UUID requestId,
    UUID recipientId,
    String channel,
    String reason,
    String details
) implements Payload {
}
