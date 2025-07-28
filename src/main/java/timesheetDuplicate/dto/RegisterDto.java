package timesheetDuplicate.dto;

import lombok.Data;
import timesheetDuplicate.entity.Role;

    @Data
    public class RegisterDto {
        private String name;
        private String email;
        private String password;
        private Role role;
        private Long managerId; // Optional for employee
    }
