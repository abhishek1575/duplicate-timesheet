package timesheetDuplicate.entity;

public enum SheetStatus {
    DRAFT,          // Not yet submitted
    PENDING,        // Submitted for approval
    APPROVED,       // Approved by manager
    REJECTED,       // Rejected by manager
    REVISED         // Resubmitted after rejection
}