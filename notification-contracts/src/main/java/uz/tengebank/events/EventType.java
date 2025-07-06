package uz.tengebank.events;

/**
 * Defines the constant string identifiers for all event types in the system.
 * Using constants from this class prevents typos and ensures consistency between
 * publishers and consumers.
 */
public final class EventType {

    private EventType() {}

    /** Fired when a new request is successfully validated and accepted by the gateway. */
    public static final String NOTIFICATION_REQUEST_ACCEPTED_V1 = "notification.request.accepted.v1";

    /** Fired when a task is placed on a specific worker queue (e.g., sms, fcm). */
    public static final String NOTIFICATION_CHANNEL_ROUTED_V1 = "notification.channel.routed.v1";

    /** Fired when a worker service consumes a task from a queue and begins processing. */
    public static final String NOTIFICATION_PROCESSING_STARTED_V1 = "notification.processing.started.v1";

    /** Fired when an external provider's API successfully accepts a notification for delivery. */
    public static final String NOTIFICATION_PROVIDER_ACCEPTED_REQUEST_V1 = "notification.provider.accepted.v1";

    /** Fired when an external provider's API immediately rejects a notification. */
    public static final String NOTIFICATION_PROVIDER_REJECTED_REQUEST_V1 = "notification.provider.rejected.v1";

    /** Fired when an internal, recoverable application error occurs (e.g., template error), potentially triggering a fallback. */
    public static final String NOTIFICATION_ATTEMPT_FAILED_V1 = "notification.attempt.failed.v1";

    /** Fired upon receiving a final delivery confirmation from a provider (e.g., via webhook). */
    public static final String NOTIFICATION_DELIVERED_V1 = "notification.delivered.v1";

    /** Fired upon receiving a final non-delivery confirmation from a provider (e.g., via webhook). */
    public static final String NOTIFICATION_UNDELIVERED_V1 = "notification.undelivered.v1";
}
