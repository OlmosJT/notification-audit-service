package uz.tengebank.notificationcontracts.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uz.tengebank.notificationcontracts.events.EventType;

import java.util.UUID;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "payloadType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationRequestPayload.class, name = "REQUEST"),
        @JsonSubTypes.Type(value = NotificationDestinationPayload.class, name = "REQUEST_DESTINATION")
})
public sealed interface Payload permits NotificationRequestPayload, NotificationDestinationPayload {
    UUID getReferenceId();
}

