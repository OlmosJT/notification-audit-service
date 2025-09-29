package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.tengebank.notificationcontracts.events.enums.NotificationRequestStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "request_status_history", schema = "audit")
@Getter @Setter
public class NotificationRequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_request_id", nullable = false)
    private NotificationRequestEntity request;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationRequestStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String details;

    @Column(nullable = false)
    private String sourceService;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime occurredAt;
}
