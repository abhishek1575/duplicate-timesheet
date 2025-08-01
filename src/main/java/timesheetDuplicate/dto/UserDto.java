package timesheetDuplicate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import timesheetDuplicate.entity.Role;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Long managerId;
    private String managerName;
    private List<Long> projectIds;
    private List<String> projectNames;
}
