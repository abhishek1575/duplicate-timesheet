package timesheetDuplicate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import timesheetDuplicate.dto.UserDto;
import timesheetDuplicate.entity.User;
import timesheetDuplicate.repository.UserRepository;
import java.util.Collections;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        timesheetDuplicate.entity.User user = userRepository.findByEmail(email)
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
            user.setManager(null); // If you want to remove the manager
        }

        userRepository.save(user);
    }
}
