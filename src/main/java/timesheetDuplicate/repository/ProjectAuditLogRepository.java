package timesheetDuplicate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import timesheetDuplicate.entity.ProjectAuditLog;

import java.util.List;

public interface ProjectAuditLogRepository extends JpaRepository<ProjectAuditLog, Long> {
    List<ProjectAuditLog> findByProjectId(Long projectId);
}
