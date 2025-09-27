package uz.tengebank.notificationcontracts.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Event published when a notification request is validated and accepted for processing.
 */
public record NotificationRequestAccepted(
        UUID requestId,
        String source,
        String category,
        String templateName,
        List<ChannelType> channels,
        DeliveryStrategy deliveryStrategy,
        Map<ChannelType, Map<String, Object>> channelConfig,
        List<Recipient> recipients
) implements Payload {
    public record Recipient(
            UUID id,
            String phone,
            String lang,
            Map<String, Object> variables
    ) {}

    public enum DeliveryStrategy {
        PARALLEL, FALLBACK;

        @JsonCreator
        public static DeliveryStrategy fromString(String value) {
            if (value == null) {
                return null;
            }

            try {
                return DeliveryStrategy.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid delivery strategy type: '" + value + "'. Accepted values are 'parallel' or 'fallback'."
                );
            }
        }
    }
}
