package uz.tengebank.payload;

import java.util.List;
import java.util.UUID;

public final class NotificationChannelRouted implements Payload {
    private UUID requestId;
    private String recipientId;
    private String routedToChannel;
    private List<String> fallbackChannels;
}
