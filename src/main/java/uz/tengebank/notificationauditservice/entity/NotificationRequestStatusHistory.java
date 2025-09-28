package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import uz.tengebank.notificationcontracts.events.enums.NotificationRequestStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "request_status_history", schema = "audit")
public class NotificationRequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_request_id", nullable = false)
    private NotificationRequest notificationRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationRequestStatus status;

    @Column(columnDefinition = "jsonb")
    private String details;

    @Column(nullable = false)
    private String sourceService;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime occurredAt;
}
