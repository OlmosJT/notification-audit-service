package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Event published by a worker service immediately after consuming a message
 * from its queue, before any other processing.
 */
public record NotificationProcessingStarted(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        String workerId
) implements Payload {}
