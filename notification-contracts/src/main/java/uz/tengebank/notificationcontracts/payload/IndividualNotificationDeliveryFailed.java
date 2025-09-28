package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.dto.FailureDetails;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Emitted based on a vendor webhook confirming delivery could not be completed.
 */
public record IndividualNotificationDeliveryFailed(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        String provider,
        String providerMessageId,
        FailureDetails reason
) implements Payload {

}
