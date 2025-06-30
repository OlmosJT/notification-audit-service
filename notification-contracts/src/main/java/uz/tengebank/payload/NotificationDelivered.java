package uz.tengebank.payload;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public final class NotificationDelivered implements Payload {
    private UUID requestId;
    private String recipientId;
    private Map<String, Object> providerDetails;
}
