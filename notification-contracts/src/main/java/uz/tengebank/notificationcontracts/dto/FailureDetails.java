package uz.tengebank.notificationcontracts.dto;

/**
 * A structured record for reporting error details in event payloads.
 *
 * @param code A machine-readable error code (e.g., "6301", "TEMPLATE_ERROR").
 * @param message A human-readable description of the error.
 */
public record FailureDetails(
        String code,
        String message
) {
}
