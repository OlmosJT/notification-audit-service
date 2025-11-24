package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.tengebank.notificationcontracts.dto.enums.NotificationRequestStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification_request_history", schema = "audit", indexes = {
        @Index(name = "idx_req_hist_req_id", columnList = "request_id"),
        @Index(name = "idx_req_hist_occurred_at", columnList = "occurredAt"),
        @Index(name = "idx_req_hist_status", columnList = "status"),
        @Index(name = "idx_req_hist_reporter_service", columnList = "reporterService")
})
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "requestId")
    private NotificationAuditRequestEntity request;

    @Column(nullable = false)
    private String reporterService;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationRequestStatus status;

    private String details;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime occurredAt;

    @PrePersist
    public void prePersist() {
        if (this.occurredAt == null) {
            this.occurredAt = OffsetDateTime.now();
        }
    }
}
