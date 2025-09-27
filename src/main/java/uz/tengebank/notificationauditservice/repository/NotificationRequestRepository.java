package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.NotificationRequest;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, Long> {
    /**
     * Finds a notification request by its unique, client-provided request ID.
     * This is the primary way to look up a batch request.
     *
     * @param requestId The UUID of the batch request.
     * @return An Optional containing the NotificationRequest if found.
     */
    Optional<NotificationRequest> findByRequestId(UUID requestId);
}
