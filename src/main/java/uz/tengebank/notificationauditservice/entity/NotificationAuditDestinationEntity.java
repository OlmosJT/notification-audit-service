package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "notification_audit_destinations",
        schema = "audit",
        indexes = {
                @Index(name = "idx_audit_dest_destination_id", columnList = "destination_id"),
                @Index(name = "idx_audit_dest_request_id", columnList = "request_id")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uq_destination_request",
                columnNames = {"request_id", "destination_id"}
        )
)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class NotificationAuditDestinationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private NotificationAuditRequestEntity request;

    @Column(nullable = false)
    private UUID destinationId;

    private String phone;
    private String email;
    private String pushToken;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> templateVariables;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationAuditDestinationEntity that)) return false;
        return Objects.equals(destinationId, that.destinationId) &&
                Objects.equals(request != null ? request.getRequestId() : null,
                        that.request != null ? that.request.getRequestId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationId, request != null ? request.getRequestId() : null);
    }

}
