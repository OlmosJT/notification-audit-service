package uz.tengebank.notificationcontracts.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import uz.tengebank.notificationcontracts.events.enums.AudienceStrategy;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;
import uz.tengebank.notificationcontracts.events.enums.DeliveryStrategy;
import uz.tengebank.notificationcontracts.utils.ValidNotificationRequest;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ValidNotificationRequest
public record NotificationRequestDto(
        @NotNull UUID requestId,
        @NotEmpty String source,
        @NotEmpty String category,
        @NotEmpty String templateName,

        @NotNull AudienceStrategy audienceStrategy,
        @NotNull DeliveryStrategy deliveryStrategy,
        @NotNull EnumSet<ChannelType> channels,
        Map<ChannelType, Map<String, Object>> channelParams,

        @Valid
        List<Recipient> recipients

) {
    /**
     * Represents a single recipient and their contact addresses for various channels.
     *
     * @param id A unique identifier for the recipient.
     * @param channelAddresses A map where the key is the channel (e.g., SMS)
     * and the value is the address (e.g., "+9989...").
     */
    public record Recipient(
            @NotNull UUID id,
            @NotEmpty Map<ChannelType, String> channelAddresses,
            Map<String, Object> templateVariables,
            String lang
    ) {

    }
}
