package timesheetDuplicate.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import timesheetDuplicate.dto.ProjectDto;
import timesheetDuplicate.entity.AuditAction;
import timesheetDuplicate.entity.Project;
import timesheetDuplicate.entity.ProjectAuditLog;
import timesheetDuplicate.entity.User;
import timesheetDuplicate.repository.ProjectAuditLogRepository;
import timesheetDuplicate.repository.ProjectRepository;
import timesheetDuplicate.repository.ProjectService;
import timesheetDuplicate.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

// ProjectServiceImpl.java
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;
    private final ProjectAuditLogRepository auditLogRepo;

    private final TimeSheetServiceImpl timeSheetService; // ✅ for logAudit reuse

    @Override
    public ProjectDto createProject(ProjectDto dto) {
        if (projectRepo.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Project with name '" + dto.getName() + "' already exists");
        }

        Project project = Project.builder()
                .name(dto.getName())
                .build();

        if (dto.getManagerId() != null) {
            User manager = userRepo.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            project.setManager(manager);
        }

        Project saved = projectRepo.save(project);

        // log who created
        User actor = getLoggedInUser();
        timeSheetService.logAudit(saved, actor, actor, AuditAction.CREATED);

        return toDto(saved);
    }

    @Override
    public void assignManager(Long projectId, Long managerId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User newManager = userRepo.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        User actor = getLoggedInUser();
        User oldManager = project.getManager();

        if (oldManager != null && !oldManager.getId().equals(newManager.getId())) {
            timeSheetService.logAudit(project, actor, oldManager, AuditAction.REMOVED_MANAGER);
        }

        project.setManager(newManager);
        projectRepo.save(project);
        timeSheetService.logAudit(project, actor, newManager, AuditAction.ASSIGNED_MANAGER);
    }

    @Override
    public void addUserToProject(Long projectId, Long userId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User actor = getLoggedInUser();

        if (!project.getTeamMembers().contains(user)) {
            project.getTeamMembers().add(user);
            projectRepo.save(project);
            timeSheetService.logAudit(project, actor, user, AuditAction.ADDED_USER);
        }
    }

    @Override
    public List<ProjectDto> getProjectsByManager(Long managerId) {
        return projectRepo.findByManagerId(managerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsByUser(Long userId) {
        return projectRepo.findByTeamMembersId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ProjectDto toDto(Project project) {
        List<User> team = project.getTeamMembers() != null ? project.getTeamMembers() : List.of();

        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .managerId(project.getManager() != null ? project.getManager().getId() : null)
                .managerName(project.getManager() != null ? project.getManager().getName() : null)
                .teamMemberIds(team.stream().map(User::getId).collect(Collectors.toList()))
                .teamMemberNames(team.stream().map(User::getName).collect(Collectors.toList()))
                .build();
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
    // path: timesheetDuplicate.service.impl.ProjectServiceImpl.java
    @Override
    public List<ProjectAuditLog> getAllLogs() {
        return auditLogRepo.findAll();
    }

}