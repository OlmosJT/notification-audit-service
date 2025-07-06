package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.tengebank.events.enums.ChannelType;
import uz.tengebank.events.enums.NotificationStatus;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "individual_notifications", schema = "audit",
        indexes = @Index(name = "idx_recipient_id", columnList = "recipientId")
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
    private String recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channelType;

    @Column(nullable = false, length = 512)
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus currentStatus;

    private String provider;

    @Column
    private String providerMessageId;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
