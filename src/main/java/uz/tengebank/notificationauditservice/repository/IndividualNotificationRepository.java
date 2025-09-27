package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.IndividualNotification;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IndividualNotificationRepository extends JpaRepository<IndividualNotification, Long> {
    /**
     * Finds a single notification by combining the parent request's UUID and the recipient's ID.
     * This is the CORRECT way to uniquely identify a single message within a batch.
     *
     * @param requestId The UUID of the parent NotificationRequest.
     * @param recipientId The ID of the individual recipient's message.
     * @return An Optional containing the IndividualNotification if found.
     */
    Optional<IndividualNotification> findByNotificationRequestRequestIdAndRecipientId(UUID requestId, String recipientId);


    /**
     * Finds a single notification by the unique message ID returned from a third-party provider.
     * This is essential for processing incoming webhooks for delivery status (DELIVERED/UNDELIVERED).
     *
     * @param providerMessageId The unique ID from the external provider (e.g., Firebase, SMSTraffic).
     * @return An Optional containing the IndividualNotification if found.
     */
    Optional<IndividualNotification> findByProviderMessageId(String providerMessageId);
}
