package uz.tengebank.notificationcontracts.events;

/**
 * Defines the constant string identifiers for all event types in the system.
 * Using constants from this class prevents typos and ensures consistency between
 * publishers and consumers.
 */
public final class EventType {

    private EventType() {}

    /** Fired when a new request is successfully validated and accepted by the gateway. */
    public static final String NOTIFICATION_REQUEST_ACCEPTED_V1 = "notification.request.accepted.v1";
}
