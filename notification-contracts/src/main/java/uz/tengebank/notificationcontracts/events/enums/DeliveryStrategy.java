package uz.tengebank.notificationcontracts.events.enums;

import uz.tengebank.notificationcontracts.utils.NIY;

public enum DeliveryStrategy {
    /** Send it via one specific channel only. */
    SINGLE,
    /** Send it on multiple channels simultaneously. */
    @NIY PARALLEL,
    /** Try channels one by one in a prioritized order. */
    @NIY FALLBACK,
    /** Choose the best channel based on user preferences. */
    @NIY USER_PREFERENCE
}
