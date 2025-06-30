package uz.tengebank.payload;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public final class NotificationUndelivered implements Payload {
    private UUID requestId;
    private String recipientId;
    private String reasonCode;
    private Map<String, Object> providerDetails;
}
