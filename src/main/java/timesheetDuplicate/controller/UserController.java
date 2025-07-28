package timesheetDuplicate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import timesheetDuplicate.dto.UserDto;
import timesheetDuplicate.dto.UserRoleInfoDto;
import timesheetDuplicate.entity.Role;
import timesheetDuplicate.entity.User;
import timesheetDuplicate.entity.UserMapper;
import timesheetDuplicate.repository.UserRepository;
import timesheetDuplicate.service.UserService;


import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;


    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> dtos = users.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/team/{managerId}")
    public ResponseEntity<List<UserDto>> getTeamByManager(@PathVariable Long managerId) {
        List<User> team = userRepository.findByManagerId(managerId);
        List<UserDto> teamDtos = team.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(teamDtos);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UserDto>> getAllManagers() {
        List<User> managers = userRepository.findByRole(Role.MANAGER);
        List<UserDto> managerDtos = managers.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(managerDtos);
    }
    
    @GetMapping("/admin")
    public ResponseEntity<List<UserDto>> getAllAdmin(){
        List<User> admin = userRepository.findByRole(Role.ADMIN);
        List<UserDto> adminDtos = admin.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(adminDtos);
    }

    @PutMapping("/assignManager")
    public ResponseEntity<?> assignManager(@RequestParam Long userId, @RequestParam Long managerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        user.setManager(manager);
        userRepository.save(user);
        return ResponseEntity.ok("Manager assigned successfully");
    }


    @PutMapping("update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto);
        return ResponseEntity.ok("User updated successfully.");
    }

    @GetMapping("/privileged")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<UserRoleInfoDto>> getPrivilegedUsers() {
        return ResponseEntity.ok(userService.getPrivilegedUsers());
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserDto>> getAllEmployees() {
        List<UserDto> employees = userService.getUsersByRole(Role.EMPLOYEE);
        return ResponseEntity.ok(employees);
    }


}
