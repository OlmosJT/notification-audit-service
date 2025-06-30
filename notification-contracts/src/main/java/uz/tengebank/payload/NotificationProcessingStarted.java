package uz.tengebank.payload;

import lombok.Data;

import java.util.UUID;

@Data
public final class NotificationProcessingStarted implements Payload {
    private UUID requestId;
    private String recipientId;
}
