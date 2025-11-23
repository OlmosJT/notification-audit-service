package uz.tengebank.notificationcontracts.events.enums;

/**
 * Represents the lifecycle of the HIGH-LEVEL Batch Request.
 * This aggregates the status of all individual destinations inside the request.
 */
public enum NotificationRequestStatus {
    /**
     * The request has been saved to the Audit DB, but the Sender Service
     * has not yet picked it up. (e.g., Sitting in the queue or scheduled for later).
     */
    ACCEPTED,
    /**
     * The request was invalid before processing started.
     * Examples: "Template not found", "Quota exceeded", "Outside allowed sending hours".
     */
    REJECTED,
    /**
     * The Sender Service has picked up the batch and is currently looping through
     * the destinations.
     */
    PROCESSING,
    /**
     * Critical System Failure. The batch could not be processed at all.
     * Example: The database connection died, or the Notification Service crashed.
     * Note: If 50/50 SMS failed due to invalid numbers, that is usually COMPLETED
     * (with 100% failure rate) or PARTIALLY_COMPLETED, not FAILED.
     */
    FAILED,
    /**
     * The batch processing finished, and EVERY single destination (100%) was
     * handed off to the provider successfully.
     */
    COMPLETED,
    /**
     * The batch processing finished, but some destinations succeeded and some failed.
     * Example: 45 Sent, 5 Failed.
     */
    PARTIALLY_COMPLETED,
    /**
     * Scheduled message stopped manually
     */
    CANCELED
}
