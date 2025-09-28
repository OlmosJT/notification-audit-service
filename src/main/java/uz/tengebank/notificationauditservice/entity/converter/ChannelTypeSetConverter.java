package uz.tengebank.notificationauditservice.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Converter
public class ChannelTypeSetConverter implements AttributeConverter<EnumSet<ChannelType>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(EnumSet<ChannelType> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public EnumSet<ChannelType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return EnumSet.noneOf(ChannelType.class);
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(ChannelType::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ChannelType.class)));
    }
}
