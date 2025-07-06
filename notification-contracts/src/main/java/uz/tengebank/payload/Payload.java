package uz.tengebank.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uz.tengebank.events.EventType;

/**
 * A sealed marker interface for all event payloads.
 * This interface uses Jackson annotations to enable polymorphic deserialization,
 * allowing the event consumer to correctly map the JSON payload to the specific
 * Java object based on the 'eventType' field in the event envelope.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationRequestAccepted.class, name = EventType.NOTIFICATION_REQUEST_ACCEPTED_V1),
        @JsonSubTypes.Type(value = NotificationChannelRouted.class, name = EventType.NOTIFICATION_CHANNEL_ROUTED_V1),
        @JsonSubTypes.Type(value = NotificationProcessingStarted.class, name = EventType.NOTIFICATION_PROCESSING_STARTED_V1),
        @JsonSubTypes.Type(value = NotificationProviderAccepted.class, name = EventType.NOTIFICATION_PROVIDER_ACCEPTED_REQUEST_V1),
        @JsonSubTypes.Type(value = NotificationProviderRejected.class, name = EventType.NOTIFICATION_PROVIDER_REJECTED_REQUEST_V1),
        @JsonSubTypes.Type(value = NotificationAttemptFailed.class, name = EventType.NOTIFICATION_ATTEMPT_FAILED_V1),
        @JsonSubTypes.Type(value = NotificationDelivered.class, name = EventType.NOTIFICATION_DELIVERED_V1),
        @JsonSubTypes.Type(value = NotificationUndelivered.class, name = EventType.NOTIFICATION_UNDELIVERED_V1)
})
public sealed interface Payload permits
        NotificationRequestAccepted,
        NotificationChannelRouted,
        NotificationProcessingStarted,
        NotificationProviderAccepted,
        NotificationProviderRejected,
        NotificationAttemptFailed,
        NotificationDelivered,
        NotificationUndelivered
{ }
