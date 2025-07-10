package timesheetDuplicate.service;

import timesheetDuplicate.dto.TimeSheetDto;
import timesheetDuplicate.dto.UserDto;

import java.util.List;
import java.util.Map;

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
    Map<UserDto, List<TimeSheetDto>> getTeamTimesheets();


}