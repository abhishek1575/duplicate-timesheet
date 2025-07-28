package timesheetDuplicate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private Long managerId;
    private String managerName;
    private List<Long> teamMemberIds;
    private List<String> teamMemberNames;
}
