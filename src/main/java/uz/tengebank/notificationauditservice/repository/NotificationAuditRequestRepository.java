package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.NotificationAuditRequestEntity;

import java.util.UUID;

@Repository
public interface NotificationAuditRequestRepository extends JpaRepository<NotificationAuditRequestEntity, UUID> {

}
