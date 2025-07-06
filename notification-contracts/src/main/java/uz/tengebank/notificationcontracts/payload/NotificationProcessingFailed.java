package uz.tengebank.notificationcontracts.payload;

import java.util.List;
import java.util.UUID;

/**
 * Event published when an entire notification request fails at a batch level,
 * before processing individual recipients.
 *
 * @param requestId The unique UUID of the original batch request.
 * @param reason A machine-readable code for the failure (e.g., "TEMPLATE_NOT_FOUND").
 * @param details A human-readable description of the error.
 * @param recipientPhones A list of all phone numbers from the original request.
 */
public record NotificationProcessingFailed(
  UUID requestId,
  String reason,
  String details,
  List<String> recipientPhones
) implements Payload {
}
