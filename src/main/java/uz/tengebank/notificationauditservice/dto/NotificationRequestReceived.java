package uz.tengebank.notificationauditservice.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class NotificationRequestReceived {

    public final String EVENT_TYPE = "notification.request.received.v1";

    private UUID requestId;
    private String requestType; // push, sms, auto
    private String templateName;
    private String fullRequestPayload;
    private List<Recipient> recipients;

    @Getter
    public static class Recipient {
        private String recipientId;
        private String destinationAddress;
        private String channelType;
    }

}
