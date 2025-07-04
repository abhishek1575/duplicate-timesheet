package timesheetDuplicate.service;

import timesheetDuplicate.dto.TimeSheetDto;

import java.util.List;

public interface TimeSheetService {
    TimeSheetDto createSheet(TimeSheetDto dto);
    TimeSheetDto updateSheet(Long id, TimeSheetDto dto);
    TimeSheetDto submitSheet(Long id);
    TimeSheetDto approveSheet(Long id);
    TimeSheetDto rejectSheet(Long id, String comments);
    TimeSheetDto resubmitSheet(Long id);
    List<TimeSheetDto> getMySheets();
    List<TimeSheetDto> getPendingSheets();
    TimeSheetDto getSheetById(Long id);
    List<TimeSheetDto> getAllSheets();
    List<TimeSheetDto> getAllDraftSheetByUserID();

}