package timesheetDuplicate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import timesheetDuplicate.entity.Role;
import timesheetDuplicate.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByEmail(String email);

    List<User> findByManagerId(Long managerId);
    List<User> findByRole(Role role);

    List<User> findByProjectsId(Long projectId);
    List<User> findByRoleAndProjectsId(Role role, Long projectId);
    List<User> findByRoleAndManagerId(Role role, Long managerId);
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoles(@Param("roles") List<Role> roles);
    List<User> findByManagerIdAndRole(Long managerId, Role role);


}

