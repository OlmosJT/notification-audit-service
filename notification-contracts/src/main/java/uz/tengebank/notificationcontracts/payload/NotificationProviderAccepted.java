package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Event published by a worker service after it successfully hands off a message
 * to a third-party provider and gets a success response.
 */
public record NotificationProviderAccepted(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        String providerName,
        String providerMessageId // The unique ID from the external vendor
) implements Payload {}
