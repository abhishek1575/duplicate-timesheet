package timesheetDuplicate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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



}
