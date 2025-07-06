package uz.tengebank.notificationcontracts.events;

/**
 * Defines the constant string identifiers for all event types in the system.
 * Using constants from this class prevents typos and ensures consistency between
 * publishers and consumers.
 */
public final class EventType {

    private EventType() {}


    public static final String NOTIFICATION_REQUEST_ACCEPTED_V1 = "notification.request.accepted.v1";
    public static final String NOTIFICATION_PROCESSING_FAILED_V1 = "notification.processing.failed.v1";
    public static final String NOTIFICATION_ATTEMPT_FAILED_V1 = "notification.attempt.failed.v1";
}
