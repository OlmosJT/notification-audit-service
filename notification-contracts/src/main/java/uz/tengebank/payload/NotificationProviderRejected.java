package uz.tengebank.payload;

import lombok.Data;

import java.util.UUID;

@Data
public final class NotificationProviderRejected implements Payload {
    private UUID requestId;
    private String recipientId;
    private String provider;
    private String reasonCode; // e.g., "INVALID_TOKEN"
    private String details; // Error message from the provider
}
