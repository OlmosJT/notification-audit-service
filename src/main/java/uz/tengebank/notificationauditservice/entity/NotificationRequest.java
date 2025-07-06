package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_requests", schema = "audit")
@Getter @Setter
public class NotificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID requestId;

    @Column(nullable = false)
    private String templateName;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String fullRequestPayload;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime receivedAt;
}
