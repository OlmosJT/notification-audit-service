package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.NotificationAuditDestinationEntity;
import uz.tengebank.notificationauditservice.entity.NotificationAuditRequestEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationAuditDestinationRepository extends JpaRepository<NotificationAuditDestinationEntity, Long> {

    Optional<NotificationAuditDestinationEntity> findByRequestRequestIdAndDestinationId(
            UUID requestId,
            UUID destinationId
    );
}
