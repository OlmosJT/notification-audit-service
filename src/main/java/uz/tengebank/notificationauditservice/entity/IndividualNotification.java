package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;
import uz.tengebank.notificationcontracts.events.enums.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notifications", schema = "audit",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_request_recipient",
                        columnNames = {"notification_request_id", "recipientId"}
                )
        },
        indexes = {
                @Index(name = "idx_recipient_id", columnList = "recipientId")
        }
)
@Getter @Setter
public class IndividualNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_request_id")
    private NotificationRequest notificationRequest;

    @Column(nullable = false)
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channelType;

    @Column(nullable = false, length = 512)
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus currentStatus;

    private String provider;

    @Column(unique = true)
    private String providerMessageId;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
