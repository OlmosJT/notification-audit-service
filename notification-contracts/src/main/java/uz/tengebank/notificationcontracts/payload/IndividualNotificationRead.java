package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Emitted when user interaction (e.g., opening a message) is detected.
 */
public record IndividualNotificationRead(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        OffsetDateTime readAt
) implements Payload {
}
