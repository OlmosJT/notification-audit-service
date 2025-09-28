package uz.tengebank.notificationcontracts.events.enums;

import uz.tengebank.notificationcontracts.utils.NIY;

public enum AudienceStrategy {
    /** A single, specific recipient. */
    DIRECT,
    /** A specific, predefined list of recipients. */
    @NIY GROUP,
    /** All users of the system. */
    @NIY BROADCAST
}
