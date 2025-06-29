package uz.tengebank.notificationauditservice.dto;

import java.util.UUID;

public class NotificationSentToProvider {
    private UUID requestId;
    private String recipientId;
    private String provider;
    private String providerMessageId;
}
