package uz.tengebank.notificationcontracts.payload;

import java.time.OffsetDateTime;

/**
 * Event published by a webhook handler when a provider confirms
 * a notification could not be delivered after being accepted.
 */
public record NotificationUndelivered(
        String providerMessageId,
        OffsetDateTime failedTimestamp,
        String reason
) implements Payload {}
