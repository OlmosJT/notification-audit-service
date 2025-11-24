package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.tengebank.notificationcontracts.dto.enums.ChannelType;
import uz.tengebank.notificationcontracts.dto.enums.Language;
import uz.tengebank.notificationcontracts.dto.enums.Priority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "notification_audit_requests",
        schema = "audit",
        indexes = {
                @Index(name = "idx_audit_req_source_system", columnList = "sourceSystem"),
                @Index(name = "idx_audit_req_channel", columnList = "channel"),
                @Index(name = "idx_audit_req_template_code", columnList = "templateCode"),
                @Index(name = "idx_audit_req_schedule_at", columnList = "scheduleAt"),
                @Index(name = "idx_audit_req_priority", columnList = "priority"),
                @Index(name = "idx_audit_req_created_at", columnList = "requestId")
        }
)
public class NotificationAuditRequestEntity {

    @Id
    private UUID requestId;

    @Enumerated(EnumType.STRING)
    private ChannelType channel;

    private String templateCode;

    @Enumerated(EnumType.STRING)
    private Language language;

    private String sourceSystem;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private boolean isBatch;

    private LocalDateTime scheduleAt;

    private Integer ttlSeconds;

    private String callbackUrl;

    private Integer retryMaxRetries;

    private Integer retryDelaySeconds;

    @Column(columnDefinition = "text[]")
    private List<String> tags;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationAuditDestinationEntity> destinations = new ArrayList<>();
}
