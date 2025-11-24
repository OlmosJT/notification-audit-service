package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.NotificationDestinationHistoryEntity;

@Repository
public interface NotificationDestinationHistoryRepository extends JpaRepository<NotificationDestinationHistoryEntity, Long> {

}
