package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.IndividualNotificationEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IndividualNotificationRepository extends JpaRepository<IndividualNotificationEntity, Long> {

    Optional<IndividualNotificationEntity> findByRequest_RequestIdAndRecipientId(UUID requestId, UUID recipientId);

    Optional<IndividualNotificationEntity> findByProviderMessageId(String providerMessageId);
}
