package uz.tengebank.notificationcontracts.payload;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Emitted when a request finishes processing and all individual notifications were successful.
 */
public record NotificationRequestCompleted (
        UUID requestId,
        OffsetDateTime completedAt
) implements Payload {
}
