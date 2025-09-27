package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.tengebank.notificationcontracts.events.enums.NotificationStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification_history", schema = "audit")
@Getter @Setter
public class NotificationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id")
    private IndividualNotification individualNotification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column
    private String providerMessageId;

    @Column
    private String reasonCode;

    @Column(columnDefinition = "jsonb")
    private String details;

    @Column(nullable = false)
    private String sourceService;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime occurredAt;
}
