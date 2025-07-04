package timesheetDuplicate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timesheetDuplicate.dto.TimeSheetDto;
import timesheetDuplicate.service.TimeSheetService;

import java.util.List;

@RestController
@RequestMapping("/sheets")
@RequiredArgsConstructor
public class TimeSheetController {

    private final TimeSheetService sheetService;

    @PostMapping("/create")
    public ResponseEntity<TimeSheetDto> create(@RequestBody TimeSheetDto dto) {
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
    public ResponseEntity<TimeSheetDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(sheetService.approveSheet(id));
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

}
