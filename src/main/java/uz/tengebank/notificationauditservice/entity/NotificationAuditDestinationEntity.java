package uz.tengebank.notificationauditservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notification_audit_destinations", schema = "audit")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class NotificationAuditDestinationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private NotificationAuditRequestEntity request;

    private UUID destinationId;

    private String phone;
    private String email;
    private String pushToken;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> templateVariables;
}
