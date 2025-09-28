package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.dto.FailureDetails;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.UUID;

/**
 * Emitted when an internal error occurs while processing a single notification.
 */
public record IndividualNotificationInternalFailure(
        UUID requestId,
        UUID recipientId,
        ChannelType channel,
        FailureDetails reason
) implements Payload {
}
