package timesheetDuplicate.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import timesheetDuplicate.dto.ProjectDto;
import timesheetDuplicate.entity.ProjectAuditLog;
import timesheetDuplicate.repository.ProjectService;
import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ProjectDto> create(@RequestBody ProjectDto dto) {
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @PutMapping("/{projectId}/assign-manager/{managerId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> assignManager(@PathVariable Long projectId, @PathVariable Long managerId) {
        projectService.assignManager(projectId, managerId);
        return ResponseEntity.ok("Manager assigned");
    }

    @PutMapping("/{projectId}/add-member/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> addMember(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.addUserToProject(projectId, userId);
        return ResponseEntity.ok("User added to project");
    }

    @GetMapping("/by-manager/{managerId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ProjectDto>> getByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(projectService.getProjectsByManager(managerId));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ProjectDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(projectService.getProjectsByUser(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ProjectDto>> getAll() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // path: timesheetDuplicate.controller.ProjectController.java
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<ProjectAuditLog>> getAuditLogs() {
        return ResponseEntity.ok(projectService.getAllLogs());
    }

}