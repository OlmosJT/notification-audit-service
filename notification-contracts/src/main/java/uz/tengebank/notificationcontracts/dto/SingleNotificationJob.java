package uz.tengebank.notificationcontracts.dto;

import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.Map;
import java.util.UUID;

public record SingleNotificationJob(
        /**
         * The ID of the parent request, used for end-to-end tracking.
         * (From: NotificationRequestDto.requestId)
         */
        UUID requestId,

        /**
         * The unique ID of the recipient this job is for.
         * (From: NotificationRequestDto.Recipient.id)
         */
        UUID recipientId,

        /**
         * The final destination address for the channel (e.g., phone number, push token, email, message ID).
         * (From: NotificationRequestDto.Recipient.channelAddresses)
         */
        String destinationAddress,

        ChannelType channel,

        /**
         * A map containing the fully rendered content from the template service.
         * The keys depend on the channel (e.g., "body" for SMS; "title", "body" for PUSH).
         */
        Map<String, String> renderedContent,

        /**
         * Specific parameters for this channel, extracted from the parent request.
         * (From: NotificationRequestDto.channelParams)
         */
        Map<String, Object> channelParams,

        /**
         * The source of the original request, for metrics and logging.
         * (From: NotificationRequestDto.source)
         */
        String source,

        /**
         * The category of the original request, for metrics and logging.
         * (From: NotificationRequestDto.category)
         */
        String category
) {
}
