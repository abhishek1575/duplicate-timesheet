package timesheetDuplicate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import timesheetDuplicate.dto.UserDto;
import timesheetDuplicate.dto.UserRoleInfoDto;
import timesheetDuplicate.entity.Role;
import timesheetDuplicate.entity.User;
import timesheetDuplicate.entity.UserMapper;
import timesheetDuplicate.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    public void updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());

        if (userDto.getManagerId() != null) {
            User manager = userRepository.findById(userDto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + userDto.getManagerId()));
            user.setManager(manager);
        } else {
            user.setManager(null);
        }

        userRepository.save(user);
    }

    public List<UserRoleInfoDto> getPrivilegedUsers() {
        List<Role> roles = List.of(Role.MANAGER, Role.ADMIN, Role.SUPER_ADMIN);
        return userRepository.findByRoles(roles).stream()
                .map(user -> UserRoleInfoDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());
    }

        public List<UserDto> getUsersByRole(Role role) {
            return userRepository.findByRole(role)
                    .stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }

}
