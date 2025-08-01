package timesheetDuplicate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import timesheetDuplicate.config.JwtUtil;
import timesheetDuplicate.dto.*;
import timesheetDuplicate.entity.AuthRequest;
import timesheetDuplicate.entity.Project;
import timesheetDuplicate.entity.Role;
import timesheetDuplicate.entity.User;
import timesheetDuplicate.repository.ProjectRepository;
import timesheetDuplicate.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepo;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            // Debugging: Log incoming request
            System.out.println("Login attempt for email: " + authRequest.getEmail());

            // 1. Verify user exists first
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> {

                        return new UsernameNotFoundException("Invalid credentials");
                    });

            // 2. Debug password verification
            System.out.println("Stored password hash: " + user.getPassword());
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }

            // 3. Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            // 4. Generate JWT token
            final String jwt = jwtUtil.generateToken(authRequest.getEmail(), user.getRole());

            // 5. Build response
            Long managerId = user.getManager() != null ? user.getManager().getId() : null;
            AuthResponse authResponse = new AuthResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().toString(),
                    jwt,
                    managerId
            );


            // Debugging: Log successful login
            System.out.println("Successful login for user: " + authRequest.getEmail());

            return ResponseEntity.ok(authResponse);

        } catch (UsernameNotFoundException e) {
            System.out.println("Login failed - user not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        } catch (BadCredentialsException e) {
            System.out.println("Login failed - bad credentials: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        } catch (Exception e) {
            System.out.println("Unexpected login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during login");
        }
    }

    @PostMapping("/public/register")
    public ResponseEntity<?> publicRegister(@RequestBody RegisterDto dto) {
        if (dto.getRole() == Role.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("EMPLOYEE registration is not allowed here.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

//    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('MANAGER')")
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody RegisterDto dto, Authentication authentication) {
//        User currentUser = userRepository.findByEmail(authentication.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (dto.getRole() != Role.EMPLOYEE) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Only EMPLOYEE registration is allowed here.");
//        }
//
//        if (userRepository.existsByEmail(dto.getEmail())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Email already exists");
//        }
//
//        User newUser = new User();
//        newUser.setName(dto.getName());
//        newUser.setEmail(dto.getEmail());
//        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
//        newUser.setRole(Role.EMPLOYEE);
//
//        // Automatically link manager if role is MANAGER
//        if (currentUser.getRole() == Role.MANAGER) {
//            newUser.setManager(currentUser);
//        } else if (dto.getManagerId() != null) {
//            newUser.setManager(userRepository.findById(dto.getManagerId()).orElse(null));
//        }
//
//        userRepository.save(newUser);
//        return ResponseEntity.ok("Employee registered successfully");
//    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto, Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getRole() != Role.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only EMPLOYEE registration is allowed here.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        User newUser = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.EMPLOYEE)
                .manager(currentUser)
                .build();

        userRepository.save(newUser);

        // ✅ Auto-assign all manager projects to this employee
        List<Project> managerProjects = projectRepo.findByManagerId(currentUser.getId());

        for (Project project : managerProjects) {
            if (!project.getTeamMembers().contains(newUser)) {
                project.getTeamMembers().add(newUser);
            }
        }

        projectRepo.saveAll(managerProjects);

        return ResponseEntity.ok("Employee registered and auto-assigned to manager's projects.");
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto dto) {
        Optional<User> userInfo = userRepository.findByEmail(dto.getEmail().trim());
        if (userInfo.isPresent()) {
            try {
                User user = userInfo.get();
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
                userRepository.save(user);
                return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully"));

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unexpected error");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse<?>> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO) {
        Optional<User> userInfo = userRepository.findById(passwordChangeDTO.getId());

        if (userInfo.isPresent()) {
            User user = userInfo.get();
            try {
                if (passwordEncoder.matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
                    user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
                    userRepository.save(user);
                    return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(ApiResponse.error("Old password is incorrect", HttpStatus.CONFLICT));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found", HttpStatus.NOT_FOUND));
        }
    }

}

