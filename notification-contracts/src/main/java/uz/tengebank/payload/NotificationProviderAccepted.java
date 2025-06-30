package uz.tengebank.payload;

import lombok.Data;

import java.util.UUID;

@Data
public final class NotificationProviderAccepted implements Payload {
    private UUID requestId;
    private String recipientId;
    private String provider;
    private String providerMessageId;
}
