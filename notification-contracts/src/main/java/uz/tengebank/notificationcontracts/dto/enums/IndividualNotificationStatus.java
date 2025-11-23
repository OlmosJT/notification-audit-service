package uz.tengebank.notificationcontracts.dto.enums;

/**
 * Represents the granular lifecycle status of a single notification sent to one recipient.
 */
public enum IndividualNotificationStatus {
    /**
     * Initial state. The individual notification has been created in the audit service
     * and is queued for processing by the notification-gateway.
     */
    ACCEPTED,
    /**
     * The notification-gateway has successfully rendered the template and published the
     * message to the correct channel-specific message queue (e.g., sms-queue).
     */
    ROUTED,
    /**
     * The channel-specific service (e.g., notification-sms-service) has successfully
     * sent the notification request to the third-party vendor. We are now awaiting a
     * final delivery report via webhook.
     */
    DISPATCHED,
    /**
     * A terminal success state. The vendor has confirmed via webhook that the message
     * was successfully delivered to the end-user's device or inbox.
     */
    DELIVERED,
    /**
     * A terminal failure state. The vendor has confirmed via webhook that the message
     * could not be delivered (e.g., invalid phone number, user blocked).
     */
    DELIVERY_FAILED,
    /**
     * A terminal failure state for any error occurring within our own system before
     * a final delivery report is received (e.g., template rendering error, vendor API
     * rejects our request, webhook is invalid).
     */
    INTERNAL_FAILURE,
    /**
     * An optional, later state after DELIVERY. Indicates that the user has
     * interacted with the notification (e.g., opened the push notification, clicked a link in the email).
     */
    READ,
    /**
     * TTL exceeded before sending
     */
    EXPIRED,
    /**
     * Scheduled message stopped manually
     */
    CANCELED

}
