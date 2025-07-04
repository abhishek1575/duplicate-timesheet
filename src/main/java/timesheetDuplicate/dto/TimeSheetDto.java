package timesheetDuplicate.dto;


import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import timesheetDuplicate.entity.SheetStatus;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSheetDto {
    private Long id;
    private String taskName;
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;
//    private Date startDate;
//    private Date endDate;
    private Double effort;
    private String project;
    private SheetStatus status;
    private Long userId;
    private String userName;
    private Long approverId;
    private String approverName;
    private Date submittedDate;
    private Date approvedDate;
    private String comments;
}