package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.tengebank.notificationcontracts.events.enums.IndividualNotificationStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "individual_notifications",
        schema = "audit",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_request_recipient",
                        columnNames = {"notification_request_id", "recipientId"}
                )
        }
)
@Getter @Setter
public class IndividualNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_request_id", nullable = false)
    private NotificationRequestEntity request;

    @Column(nullable = false)
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndividualNotificationStatus currentStatus;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "individualNotification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndividualNotificationStatusHistory> statusHistory = new ArrayList<>();
}
