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
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_audit_requests", schema = "audit")
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

    @Column(columnDefinition = "text[]")
    private List<String> tags;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<NotificationAuditDestinationEntity> destinations;
}
