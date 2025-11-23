package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.NotificationAuditDestinationEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationAuditDestination extends JpaRepository<NotificationAuditDestinationEntity, Long> {

    Optional<NotificationAuditDestinationEntity> findByRequest_RequestIdAndRecipientId(UUID requestId, UUID recipientId);

    Optional<NotificationAuditDestinationEntity> findByProviderMessageId(String providerMessageId);
}
