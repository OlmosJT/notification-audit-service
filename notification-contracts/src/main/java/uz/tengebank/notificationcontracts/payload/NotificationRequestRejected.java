package uz.tengebank.notificationcontracts.payload;

import java.util.Map;
import java.util.UUID;

/**
 * Event published by the gateway when a request is rejected before being queued,
 * due to validation errors or other pre-flight checks like a missing template.
 */
public record NotificationRequestRejected(
        UUID requestId,
        String source,
        String reason,
        Map<String, String> details
) implements Payload {
}
