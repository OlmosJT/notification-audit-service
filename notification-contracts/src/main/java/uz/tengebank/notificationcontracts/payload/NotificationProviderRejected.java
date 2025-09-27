package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Event published by a worker service when a third-party provider actively
 * rejects a message with a specific error (e.g., invalid phone number).
 */
public record NotificationProviderRejected(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        String providerName,
        String reason,
        String details
) implements Payload {}
