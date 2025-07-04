package timesheetDuplicate.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sheet")
public class TimeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String taskName;

    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

//    @NotNull
//    @FutureOrPresent(message = "Start date must be today or in the future")
//    private Date startDate;
//
//    @NotNull
//    @FutureOrPresent(message = "End date must be today or in the future")
//    private Date endDate;

    @Min(0)
    @Max(24)
    private Double effort; // Hours per day

    @NotBlank
    private String project;

    @Enumerated(EnumType.STRING)
    private SheetStatus status = SheetStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    private Date submittedDate;
    private Date approvedDate;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = SheetStatus.DRAFT;
        }
    }

    @Column(length = 1000)
    private String comments;
}
