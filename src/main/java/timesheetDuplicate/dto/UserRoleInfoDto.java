package timesheetDuplicate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import timesheetDuplicate.entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleInfoDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
