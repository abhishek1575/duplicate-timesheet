package timesheetDuplicate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private String projectName;

    private Long actorId; // who performed the change
    private String actorName;

    private Long targetUserId; // user affected (assigned/removed)
    private String targetUserName;

    @Enumerated(EnumType.STRING)
    private AuditAction action; // ADDED, REMOVED, MANAGER_CHANGED

    private LocalDateTime timestamp;
}
