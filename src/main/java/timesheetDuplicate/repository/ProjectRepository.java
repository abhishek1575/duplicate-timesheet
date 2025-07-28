package timesheetDuplicate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import timesheetDuplicate.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManagerId(Long managerId);
    List<Project> findByTeamMembersId(Long userId);
    Optional<Project> findByName(String name);
}