package uz.tengebank.notificationcontracts.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uz.tengebank.notificationcontracts.dto.NotificationRequestDto;
import uz.tengebank.notificationcontracts.events.enums.AudienceStrategy;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.regex.Pattern;

public class NotificationRequestValidator implements ConstraintValidator<ValidNotificationRequest, NotificationRequestDto> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");


    @Override
    public boolean isValid(NotificationRequestDto request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean isValid = true;

        context.disableDefaultConstraintViolation();

        if (!validateAudience(request, context)) {
            isValid = false;
        }

        if (request.recipients() != null) {
            for (int i = 0; i < request.recipients().size(); i++) {
                NotificationRequestDto.Recipient recipient = request.recipients().get(i);
                if (!validateRecipientChannels(recipient, i, context)) {
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    private boolean validateAudience(NotificationRequestDto request, ConstraintValidatorContext context) {
        int recipientCount = (request.recipients() == null) ? 0 : request.recipients().size();

        if (request.audienceStrategy() == AudienceStrategy.DIRECT && recipientCount != 1) {
            context.buildConstraintViolationWithTemplate("Recipient list must contain exactly one recipient for DIRECT strategy.")
                    .addPropertyNode("recipients").addConstraintViolation();
            return false;
        }

        if (request.audienceStrategy() == AudienceStrategy.GROUP && recipientCount < 1) {
            context.buildConstraintViolationWithTemplate("Recipient list cannot be empty for GROUP strategy.")
                    .addPropertyNode("recipients").addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validateRecipientChannels(NotificationRequestDto.Recipient recipient, int index, ConstraintValidatorContext context) {
        boolean allChannelsValid = true;
        if (recipient.channelAddresses() == null || recipient.channelAddresses().isEmpty()) {
            context.buildConstraintViolationWithTemplate("channelAddresses must not be null or empty.")
                    .addPropertyNode("recipients[" + index + "].channelAddresses").addConstraintViolation();
            return false;
        }

        for (var entry : recipient.channelAddresses().entrySet()) {
            ChannelType channel = entry.getKey();
            String address = entry.getValue();

            if (address == null || address.isBlank()) {
                buildChannelViolation(context, index, channel.name(), "Address cannot be blank.");
                allChannelsValid = false;
                continue;
            }

            boolean currentChannelValid = switch (channel) {
                case SMS -> PHONE_PATTERN.matcher(address).matches();
                case EMAIL -> EMAIL_PATTERN.matcher(address).matches();
                case PUSH, TELEGRAM -> !address.isBlank();
            };

            if (!currentChannelValid) {
                buildChannelViolation(context, index, channel.name(), "Invalid format for " + channel + " address.");
                allChannelsValid = false;
            }
        }
        return allChannelsValid;
    }

    private void buildChannelViolation(ConstraintValidatorContext context, int index, String channelName, String message) {
        String path = String.format("recipients[%d].channelAddresses[%s]", index, channelName);
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(path).addConstraintViolation();
    }
}
