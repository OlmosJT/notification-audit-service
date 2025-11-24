package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.tengebank.notificationauditservice.entity.NotificationRequestHistoryEntity;

@Repository
public interface NotificationRequestHistoryRepository extends JpaRepository<NotificationRequestHistoryEntity, Long> {

}
