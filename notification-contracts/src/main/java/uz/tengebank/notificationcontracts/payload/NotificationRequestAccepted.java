package uz.tengebank.notificationcontracts.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.EnumSet;
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
        String fullRequestPayloadAsJson
) implements Payload {

}
