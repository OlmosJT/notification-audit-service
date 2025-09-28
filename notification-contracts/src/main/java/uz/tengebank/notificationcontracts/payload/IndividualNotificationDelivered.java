package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Emitted based on a vendor webhook confirming successful delivery.
 */
public record IndividualNotificationDelivered(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        String provider,
        String providerMessageId,
        OffsetDateTime deliveredAt
) implements Payload {

}
