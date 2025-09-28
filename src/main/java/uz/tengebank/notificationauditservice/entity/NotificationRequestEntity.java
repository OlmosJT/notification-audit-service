package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.tengebank.notificationauditservice.entity.converter.ChannelTypeSetConverter;
import uz.tengebank.notificationcontracts.events.enums.AudienceStrategy;
import uz.tengebank.notificationcontracts.events.enums.ChannelType;
import uz.tengebank.notificationcontracts.events.enums.DeliveryStrategy;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notification_requests", schema = "audit")
@Getter @Setter
public class NotificationRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID requestId;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String category;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience_strategy", nullable = false)
    private AudienceStrategy audienceStrategy;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_strategy", nullable = false)
    private DeliveryStrategy deliveryStrategy;

    @Convert(converter = ChannelTypeSetConverter.class)
    @Column(name = "channels", nullable = false)
    private EnumSet<ChannelType> channels;

    @Column(name = "channel_params", columnDefinition = "jsonb")
    private String channelParams;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String fullRequestPayload;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime receivedAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndividualNotificationEntity> individualNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationRequestStatusHistory> statusHistory = new ArrayList<>();
}
