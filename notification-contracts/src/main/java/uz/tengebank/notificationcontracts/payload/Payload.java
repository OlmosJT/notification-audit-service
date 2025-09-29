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

