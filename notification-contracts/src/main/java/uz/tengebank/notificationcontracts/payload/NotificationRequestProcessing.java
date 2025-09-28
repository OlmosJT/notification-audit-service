package uz.tengebank.notificationcontracts.payload;

import java.util.UUID;

public record NotificationRequestProcessing(
        UUID requestId
) implements Payload {
}
