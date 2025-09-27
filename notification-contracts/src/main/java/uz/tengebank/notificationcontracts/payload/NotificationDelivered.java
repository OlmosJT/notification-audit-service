package uz.tengebank.notificationcontracts.payload;

import java.time.OffsetDateTime;

/**
 * Event published by a webhook handler when a provider confirms
 * successful delivery of a notification to the end device.
 */
public record NotificationDelivered(
        String providerMessageId,
        OffsetDateTime deliveredTimestamp
) implements Payload {}
