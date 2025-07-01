package timesheetDuplicate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import timesheetDuplicate.entity.User;
import timesheetDuplicate.repository.UserRepository;
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
            AuthResponse authResponse = new AuthResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().toString(),
                    jwt
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        // Set manager if provided
        if (dto.getManagerId() != null) {
            user.setManager(userRepository.findById(dto.getManagerId()).orElse(null));
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto dto) {
        Optional<User> userInfo = userRepository.findByEmail(dto.getEmail().trim());
        if (userInfo.isPresent()) {
            try {
                User user = userInfo.get();
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
                userRepository.save(user);
                return ResponseEntity.ok("Password changed successfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unexpected error");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO){
        Optional<User> userInfo = userRepository.findById(passwordChangeDTO.getId());

        if (userInfo.isPresent()){
            User user = userInfo.get();
            try{
                if(passwordEncoder.matches(passwordChangeDTO.getOldPassword(),user.getPassword())){
                    user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
                    userRepository.save(user);
                    return ResponseEntity.ok(new ApiResponse("Password changed successfully", true));
                }else {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Old Password is Wrong");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unexpected Error");
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User Not Found");
        }
    }
}

