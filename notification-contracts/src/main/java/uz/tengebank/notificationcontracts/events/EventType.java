package uz.tengebank.notificationcontracts.events;

/**
 * Defines the constant string identifiers for all event types in the system.
 * Using constants from this class prevents typos and ensures consistency between
 * publishers and consumers.
 */
public final class EventType {

    private EventType() {}

    // GATEWAY Service Events
    public static final String NOTIFICATION_REQUEST_ACCEPTED_V1 = "notification.request.accepted.v1";
    public static final String NOTIFICATION_REQUEST_REJECTED_V1 = "notification.request.rejected.v1";
    public static final String NOTIFICATION_CHANNEL_ROUTED_V1 = "notification.channel.routed.v1";


    // Worker (notification-sms-service | notification-fsm-service) Service Events
    public static final String NOTIFICATION_PROCESSING_STARTED_V1 = "notification.processing.started.v1";
    public static final String NOTIFICATION_PROVIDER_ACCEPTED_V1 = "notification.provider.accepted.v1";
    public static final String NOTIFICATION_PROVIDER_REJECTED_V1 = "notification.provider.rejected.v1";
    public static final String NOTIFICATION_ATTEMPT_FAILED_V1 = "notification.attempt.failed.v1";


    // WEBHOOK/CALLBACK Events
    public static final String NOTIFICATION_DELIVERED_V1 = "notification.delivered.v1";
    public static final String NOTIFICATION_UNDELIVERED_V1 = "notification.undelivered.v1";

}