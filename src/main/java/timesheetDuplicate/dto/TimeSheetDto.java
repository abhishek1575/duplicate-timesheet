package timesheetDuplicate.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import timesheetDuplicate.entity.SheetStatus;

import java.time.LocalDate;
import java.util.Date;


import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSheetDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("taskName")
    private String taskName;

    @FutureOrPresent(message = "Start date must be today or in the future")
    @JsonProperty("startDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @FutureOrPresent(message = "End date must be today or in the future")
    @JsonProperty("endDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonProperty("effort")
    private Double effort;

    @JsonProperty("projectId")
    private Long projectId;

    @JsonProperty("projectName")
    private String projectName;

    @JsonProperty("status")
    private SheetStatus status;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("approverId")
    private Long approverId;

    @JsonProperty("approverName")
    private String approverName;

    @JsonProperty("submittedDate")
    private Date submittedDate;

    @JsonProperty("approvedDate")
    private Date approvedDate;

    @JsonProperty("comments")
    private String comments;
}


//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class TimeSheetDto {
//    private Long id;
//    private String taskName;
//
//    @FutureOrPresent(message = "Start date must be today or in the future")
//    private LocalDate startDate;
//
//    @FutureOrPresent(message = "End date must be today or in the future")
//    private LocalDate endDate;
//
//    private Double effort;
//    private Long projectId;
//    private String projectName;
//    private SheetStatus status;
//    private Long userId;
//    private String userName;
//    private Long approverId;
//    private String approverName;
//    private Date submittedDate;
//    private Date approvedDate;
//    private String comments;
//}
