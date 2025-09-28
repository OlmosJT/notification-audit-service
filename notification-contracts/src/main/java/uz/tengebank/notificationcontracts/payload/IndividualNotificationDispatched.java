package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Emitted when a channel service successfully sends a message to a third-party vendor.
 */
public record IndividualNotificationDispatched(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        String provider,
        String providerMessageId
) implements Payload {
}
