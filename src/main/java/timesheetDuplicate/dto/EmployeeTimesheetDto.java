package timesheetDuplicate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTimesheetDto {
    private Long employeeId;
    private String employeeName;
    private String email;
    private List<TimeSheetDto> timesheets;
}

