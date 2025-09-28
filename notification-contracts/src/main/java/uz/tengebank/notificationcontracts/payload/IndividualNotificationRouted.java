package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Emitted when a message for a single recipient is routed to a channel-specific queue.
 */
public record IndividualNotificationRouted(
        UUID requestId,
        UUID recipientId,
        ChannelType channel
) implements Payload {
}
