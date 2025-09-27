package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Event published by the gateway after successfully placing a message
 * for a specific recipient and channel into its designated queue.
 */
public record NotificationChannelRouted(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        String queueName
) implements Payload {
}
