package uz.tengebank.notificationcontracts.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uz.tengebank.notificationcontracts.events.EventType;

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
        @JsonSubTypes.Type(value = NotificationRequestRejected.class, name = EventType.NOTIFICATION_REQUEST_REJECTED_V1),
        @JsonSubTypes.Type(value = NotificationRequestProcessing.class, name = EventType.NOTIFICATION_REQUEST_PROCESSING_V1),
        @JsonSubTypes.Type(value = NotificationRequestFailed.class, name = EventType.NOTIFICATION_REQUEST_FAILED_V1),
        @JsonSubTypes.Type(value = NotificationRequestCompleted.class, name = EventType.NOTIFICATION_REQUEST_COMPLETED_V1),
        @JsonSubTypes.Type(value = NotificationRequestPartiallyCompleted.class, name = EventType.NOTIFICATION_REQUEST_PARTIALLY_COMPLETED_V1),

        @JsonSubTypes.Type(value = IndividualNotificationAccepted.class, name = EventType.INDIVIDUAL_NOTIFICATION_ACCEPTED_V1),
        @JsonSubTypes.Type(value = IndividualNotificationRouted.class, name = EventType.INDIVIDUAL_NOTIFICATION_ROUTED_V1),
        @JsonSubTypes.Type(value = IndividualNotificationDispatched.class, name = EventType.INDIVIDUAL_NOTIFICATION_DISPATCHED_V1),
        @JsonSubTypes.Type(value = IndividualNotificationDelivered.class, name = EventType.INDIVIDUAL_NOTIFICATION_DELIVERED_V1),
        @JsonSubTypes.Type(value = IndividualNotificationDeliveryFailed.class, name = EventType.INDIVIDUAL_NOTIFICATION_DELIVERY_FAILED_V1),
        @JsonSubTypes.Type(value = IndividualNotificationInternalFailure.class, name = EventType.INDIVIDUAL_NOTIFICATION_INTERNAL_FAILURE_V1),
        @JsonSubTypes.Type(value = IndividualNotificationRead.class, name = EventType.INDIVIDUAL_NOTIFICATION_READ_V1),
})
public sealed interface Payload permits
        NotificationRequestAccepted,
        NotificationRequestRejected,
        NotificationRequestProcessing,
        NotificationRequestFailed,
        NotificationRequestCompleted,
        NotificationRequestPartiallyCompleted,

        IndividualNotificationAccepted,
        IndividualNotificationRouted,
        IndividualNotificationDispatched,
        IndividualNotificationDelivered,
        IndividualNotificationDeliveryFailed,
        IndividualNotificationInternalFailure,
        IndividualNotificationRead {
}

