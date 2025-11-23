package uz.tengebank.notificationcontracts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;
import uz.tengebank.notificationcontracts.events.enums.Language;
import uz.tengebank.notificationcontracts.events.enums.Priority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record NotificationRequest(
        @NotNull(message = "Request ID is required")
        UUID requestId,
        @NotNull(message = "Channel type is required")
        ChannelType channel,
        @NotBlank(message = "Template code is required")
        String templateCode,
        @NotNull(message = "Language code is required")
        Language language,
        @Valid
        @NotNull
        @Size(min = 1, max = 50, message = "Range destination is 1-50")
        List<Destination> destinations,
        @Valid
        @NotNull
        Metadata metadata
) {
    /**
     * Represents a single target for the notification.
     * Corresponds to the objects in the "destinations" array.
     */
    public record Destination(
            @NotNull(message = "Destination ID is required")
            UUID destinationId,
            @Pattern(
                    regexp = "^\\+?998[0-9]{9}$",
                    message = "Invalid phone number format. Must be a valid Uzbekistan mobile number (e.g., +998901234567)"
            )
            String phone,
            @Size(min = 32, message = "Push token seems too short to be valid")
            String pushToken,
            String email,
            Map<String, Object> variables
    ) {
    }

    /**
     * Meta-information regarding the processing of the request.
     */
    public record Metadata(
            @NotNull
            Priority priority,
            boolean batch,
            @NotBlank
            String source,
            Integer ttlSeconds,
            String callbackUrl,
            List<String> tags,
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime scheduleAt,
            @Valid
            RetryPolicy retryPolicy
    ) {
    }

    public record RetryPolicy(
            @Min(1)
            Integer maxRetries,
            @Min(1)
            Integer retryDelaySeconds
    ) {
    }
}
