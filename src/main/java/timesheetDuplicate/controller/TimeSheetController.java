package timesheetDuplicate.controller;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import timesheetDuplicate.dto.EmployeeTimesheetDto;
import timesheetDuplicate.dto.TimeSheetDto;
import timesheetDuplicate.dto.UserDto;
import timesheetDuplicate.entity.Role;
import timesheetDuplicate.entity.SheetStatus;
import timesheetDuplicate.entity.TimeSheet;
import timesheetDuplicate.entity.User;
import timesheetDuplicate.service.TimeSheetService;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/sheets")
@RequiredArgsConstructor
public class TimeSheetController {

    private final TimeSheetService sheetService;

//    @PostMapping("/create")
//    public ResponseEntity<TimeSheetDto> create(@RequestBody TimeSheetDto dto) {
//        return ResponseEntity.ok(sheetService.createSheet(dto));
//    }

    @PostMapping("/create")
    public ResponseEntity<TimeSheetDto> create(@RequestBody Map<String, Object> raw) {
        System.out.println("🧾 Raw JSON received: " + raw);
        System.out.println("🧾 Raw JSON received: " + raw);
        System.out.println("👉 projectId key present? " + raw.containsKey("projectId"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        TimeSheetDto dto = mapper.convertValue(raw, TimeSheetDto.class);


        System.out.println("📦 Converted DTO projectId: " + dto.getProjectId());

        return ResponseEntity.ok(sheetService.createSheet(dto));
    }



    @PutMapping("/{id}")
    public ResponseEntity<TimeSheetDto> update(@PathVariable Long id, @RequestBody TimeSheetDto dto) {
        return ResponseEntity.ok(sheetService.updateSheet(id, dto));
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<TimeSheetDto> submit(@PathVariable Long id) {
        return ResponseEntity.ok(sheetService.submitSheet(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sheetService.approveSheet(id));
        } catch (Exception e) {
            e.printStackTrace();  // Force print full stacktrace in console
            throw e;  // Rethrow to your global handler
        }
    }


    @PutMapping("/{id}/reject")
    public ResponseEntity<TimeSheetDto> reject(@PathVariable Long id, @RequestParam String comment) {
        return ResponseEntity.ok(sheetService.rejectSheet(id, comment));
    }

    @PutMapping("/{id}/resubmit")
    public ResponseEntity<TimeSheetDto> resubmit(@PathVariable Long id) {
        return ResponseEntity.ok(sheetService.resubmitSheet(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TimeSheetDto>> mySheets() {
        return ResponseEntity.ok(sheetService.getMySheets());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TimeSheetDto>> pendingSheets() {
        return ResponseEntity.ok(sheetService.getPendingSheets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSheetDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sheetService.getSheetById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TimeSheetDto>> allSheets() {
        return ResponseEntity.ok(sheetService.getAllSheets());
    }

    @GetMapping("/draft")
    public ResponseEntity<List<TimeSheetDto>> getAllDraftSheets() {
        return ResponseEntity.ok(sheetService.getAllDraftSheetByUserID());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<TimeSheetDto>> getAllRejectSheet(){
        return ResponseEntity.ok(sheetService.getAllRejectSheetByUserId());
    }

    @GetMapping("/team-timesheets")
    public ResponseEntity<Map<UserDto, List<TimeSheetDto>>> getTeamTimesheets() {
        return ResponseEntity.ok(sheetService.getTeamTimesheets());
    }

    @GetMapping("/admin/all-employee-timesheets")
    public ResponseEntity<List<EmployeeTimesheetDto>> getAllEmployeeTimesheets() {
        List<EmployeeTimesheetDto> result = sheetService.getAllEmployeesWithTimesheets();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/pending-manager-sheets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimeSheetDto>> getPendingManagerSheets() {
        return ResponseEntity.ok(sheetService.getPendingManagerSheets());
    }


}
