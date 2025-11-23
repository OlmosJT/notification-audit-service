package uz.tengebank.notificationcontracts.events.enums;

import lombok.Getter;

@Getter
public enum Language {
    EN("English"),
    UZ("Uzbek"),
    RU("Russian"),
    KZ("Kazakh");

    private final String description;

    Language(String description) {
        this.description = description;
    }


    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.name().equalsIgnoreCase(code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unsupported language code: " + code);
    }
}
