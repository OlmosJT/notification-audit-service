package uz.tengebank.notificationauditservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationStatusHistoryRepository extends JpaRepository<NotificationStatusHistory, Long> {
    /**
     * Retrieves the complete, ordered history of status changes for a single notification.
     * This will power the detailed timeline view in your monitoring UI.
     *
     * @param notificationId The primary key of the IndividualNotification.
     * @return A list of history records, ordered from oldest to newest.
     */
    List<NotificationStatusHistory> findByIndividualNotificationIdOrderByOccurredAtAsc(Long notificationId);
}
