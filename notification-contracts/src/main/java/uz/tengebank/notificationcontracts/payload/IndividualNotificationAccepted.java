package uz.tengebank.notificationcontracts.payload;

import uz.tengebank.notificationcontracts.dto.SingleNotificationJob;

import java.util.UUID;

public record IndividualNotificationAccepted(
    SingleNotificationJob recipientDto
) implements Payload {
}
