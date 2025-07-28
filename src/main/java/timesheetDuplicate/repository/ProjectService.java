package timesheetDuplicate.repository;

import timesheetDuplicate.dto.ProjectDto;
import timesheetDuplicate.entity.ProjectAuditLog;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(ProjectDto dto);
    void assignManager(Long projectId, Long managerId);
    void addUserToProject(Long projectId, Long userId);
    List<ProjectDto> getProjectsByManager(Long managerId);
    List<ProjectDto> getProjectsByUser(Long userId);
    List<ProjectDto> getAllProjects();
    // path: timesheetDuplicate.service.ProjectService.java
    List<ProjectAuditLog> getAllLogs();

}