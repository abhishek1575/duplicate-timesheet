package timesheetDuplicate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import timesheetDuplicate.entity.SheetStatus;
import timesheetDuplicate.entity.TimeSheet;

import java.util.List;

public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {
    List<TimeSheet> findByUserId(Long userId);
    List<TimeSheet> findByUserManagerIdAndStatus(Long managerId, SheetStatus status);
    List<TimeSheet> findByUserIdAndStatus(Long userId, SheetStatus status);
    List<TimeSheet> findByStatus(SheetStatus status);
    List<TimeSheet> findAll();

    List<TimeSheet> findByProjectId(Long projectId);
    List<TimeSheet> findByApproverId(Long approverId);
    List<TimeSheet> findByProjectIdAndStatus(Long projectId, SheetStatus status);
    List<TimeSheet> findByUserIdAndProjectId(Long userId, Long projectId);
}