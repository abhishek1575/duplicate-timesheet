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

    private LocalDate startDate;

    private LocalDate endDate;

    @Min(0)
    private Double effort;

    @Enumerated(EnumType.STRING)
    private SheetStatus status = SheetStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

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

    @Column(name = "project_name")
    private String projectName;

}