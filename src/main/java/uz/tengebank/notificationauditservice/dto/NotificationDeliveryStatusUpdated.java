package uz.tengebank.notificationauditservice.dto;

import java.util.Map;
import java.util.UUID;

public class NotificationDeliveryStatusUpdated {
    private UUID requestId;
    private String recipientId;
    private String newStatus; // e.g., "DELIVERED", "UNDELIVERED"
    private Map<String, Object> providerDetails; // The full callback payload from the provider

}
