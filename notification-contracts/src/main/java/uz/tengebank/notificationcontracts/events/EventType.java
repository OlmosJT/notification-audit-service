package uz.tengebank.notificationcontracts.events;

/**
 * Defines the constant string identifiers for all event types in the system.
 * Using constants from this class prevents typos and ensures consistency between
 * publishers and consumers.
 */
public final class EventType {

    private EventType() {}

    // NotificationRequest Level Events
    public static final String NOTIFICATION_REQUEST_ACCEPTED_V1 = "notification.request.accepted.v1";
    public static final String NOTIFICATION_REQUEST_REJECTED_V1 = "notification.request.rejected.v1";
    public static final String NOTIFICATION_REQUEST_PROCESSING_V1 = "notification.request.processing.v1";
    public static final String NOTIFICATION_REQUEST_FAILED_V1 = "notification.request.failed.v1";
    public static final String NOTIFICATION_REQUEST_COMPLETED_V1 = "notification.request.completed.v1";
    public static final String NOTIFICATION_REQUEST_PARTIALLY_COMPLETED_V1 = "notification.request.partially_completed.v1";


    // IndividualNotification Level Events
    public static final String INDIVIDUAL_NOTIFICATION_ACCEPTED_V1 = "individual.notification.accepted.v1";
    public static final String INDIVIDUAL_NOTIFICATION_ROUTED_V1 = "individual.notification.routed.v1";
    public static final String INDIVIDUAL_NOTIFICATION_DISPATCHED_V1 = "individual.notification.dispatched.v1";
    public static final String INDIVIDUAL_NOTIFICATION_DELIVERED_V1 = "individual.notification.delivered.v1";
    public static final String INDIVIDUAL_NOTIFICATION_DELIVERY_FAILED_V1 = "individual.notification.delivery_failed.v1";
    public static final String INDIVIDUAL_NOTIFICATION_INTERNAL_FAILURE_V1 = "individual.notification.internal_failure.v1";
    public static final String INDIVIDUAL_NOTIFICATION_READ_V1 = "individual.notification.read.v1";

}