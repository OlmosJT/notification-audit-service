package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.tengebank.notificationcontracts.events.enums.IndividualNotificationStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification_destination_history", schema = "audit", indexes = {
        @Index(name = "idx_dest_hist_dest_id", columnList = "audit_destination_id"),
        @Index(name = "idx_dest_hist_occurred_at", columnList = "occurredAt")
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
