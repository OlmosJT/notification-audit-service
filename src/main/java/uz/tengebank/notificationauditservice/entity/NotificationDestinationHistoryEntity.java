package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.tengebank.notificationcontracts.dto.enums.IndividualNotificationStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification_destination_history", schema = "audit", indexes = {
        @Index(name = "idx_dest_hist_dest_id", columnList = "audit_destination_id"),
        @Index(name = "idx_dest_hist_occurred_at", columnList = "occurredAt"),
        @Index(name = "idx_dest_hist_event_type", columnList = "eventType"),
        @Index(name = "idx_dest_hist_status", columnList = "status")
})
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDestinationHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_destination_id", nullable = false)
    private NotificationAuditDestinationEntity destination;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false)
    private String reporterService;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndividualNotificationStatus status;

    @Column(length = 1000)
    private String errorMessage;

    @Column(columnDefinition = "text")
    private String providerRawResponse;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime occurredAt;

    @PrePersist
    public void prePersist() {
        if (this.occurredAt == null) {
            this.occurredAt = OffsetDateTime.now();
        }
    }
}
