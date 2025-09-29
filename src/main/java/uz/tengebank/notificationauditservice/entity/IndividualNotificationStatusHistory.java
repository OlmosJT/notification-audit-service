package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.tengebank.notificationcontracts.events.enums.IndividualNotificationStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "individual_notification_status_history", schema = "audit")
@Getter @Setter
public class IndividualNotificationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "individual_notification_id", nullable = false)
    private IndividualNotificationEntity individualNotification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndividualNotificationStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String details;

    @Column(nullable = false)
    private String sourceService;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime occurredAt;
}
