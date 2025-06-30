package uz.tengebank.payload;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public final class NotificationRequestAccepted implements Payload {
    private UUID requestId;
    private String requestType;
    private String templateName;
    private String fullRequestPayload;
    private List<Recipient> recipients;

    @Data
    public static final class Recipient {
        private String recipientId;
        private String destinationAddress;
        private String channelType;
    }
}
