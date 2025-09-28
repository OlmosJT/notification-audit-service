package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.payload.Payload;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Emitted when a request finishes processing with a mix of successful and failed individual notifications.
 */
public record NotificationRequestPartiallyCompleted (
        UUID requestId,
        OffsetDateTime completedAt,
        int successCount,
        int failureCount
) implements Payload {
}
