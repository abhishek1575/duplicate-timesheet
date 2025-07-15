package timesheetDuplicate.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import timesheetDuplicate.dto.EmployeeTimesheetDto;
import timesheetDuplicate.dto.TimeSheetDto;
import timesheetDuplicate.dto.UserDto;
import timesheetDuplicate.entity.*;
import timesheetDuplicate.repository.TimeSheetRepository;
import timesheetDuplicate.repository.UserRepository;
import timesheetDuplicate.service.TimeSheetService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSheetServiceImpl implements TimeSheetService {

    private final TimeSheetRepository timeSheetRepo;
    private final UserRepository userRepo;
    private final UserMapper userMapper;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private TimeSheetDto toDto(TimeSheet ts) {
        return TimeSheetDto.builder()
                .id(ts.getId())
                .taskName(ts.getTaskName())
                .startDate(ts.getStartDate())
                .endDate(ts.getEndDate())
                .effort(ts.getEffort())
                .project(ts.getProject())
                .status(ts.getStatus())
                .userId(ts.getUser().getId())
                .userName(ts.getUser().getName())
                .approverId(ts.getApprover() != null ? ts.getApprover().getId() : null)
                .approverName(ts.getApprover() != null ? ts.getApprover().getName() : null)
                .submittedDate(ts.getSubmittedDate())
                .approvedDate(ts.getApprovedDate())
                .comments(ts.getComments())
                .build();
    }

    @Override
    public TimeSheetDto createSheet(TimeSheetDto dto) {
        User user = getLoggedInUser();
        TimeSheet ts = TimeSheet.builder()
                .taskName(dto.getTaskName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .effort(dto.getEffort())
                .project(dto.getProject())
                .status(SheetStatus.DRAFT)
                .user(user)
                .build();
        return toDto(timeSheetRepo.save(ts));
    }

//    @Transactional
//    @Override
//    public TimeSheetDto updateSheet(Long id, TimeSheetDto dto) {
//        TimeSheet ts = timeSheetRepo.findById(id).orElseThrow(() -> new RuntimeException("Sheet not found"));
//        if (ts.getStatus() != SheetStatus.DRAFT && ts.getStatus() != SheetStatus.REVISED) {
//            throw new RuntimeException("Cannot update submitted/approved sheet");
//        }
//        ts.setTaskName(dto.getTaskName());
//        ts.setStartDate(dto.getStartDate());
//        ts.setEndDate(dto.getEndDate());
//        ts.setEffort(dto.getEffort());
//        ts.setProject(dto.getProject());
//        return toDto(timeSheetRepo.save(ts));
//    }

    @Transactional
    @Override
    public TimeSheetDto updateSheet(Long id, TimeSheetDto dto) {
        TimeSheet ts = timeSheetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sheet not found"));

        if (ts.getStatus() == SheetStatus.PENDING || ts.getStatus() == SheetStatus.APPROVED) {
            throw new RuntimeException("Cannot update submitted/approved sheet");
        }

        ts.setTaskName(dto.getTaskName());
        ts.setStartDate(dto.getStartDate());
        ts.setEndDate(dto.getEndDate());
        ts.setEffort(dto.getEffort());
        ts.setProject(dto.getProject());

        return toDto(timeSheetRepo.save(ts));
    }



    @Override
    @Transactional
    public TimeSheetDto submitSheet(Long id) {
        TimeSheet ts = timeSheetRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sheet not found"));

        if (ts.getStatus() != SheetStatus.DRAFT && ts.getStatus() != SheetStatus.REVISED) {
            throw new RuntimeException("Only draft or revised sheets can be submitted");
        }

        User user = getLoggedInUser();
        ts.setStatus(SheetStatus.PENDING);
        ts.setSubmittedDate(new Date());
        ts.setApprover(user.getManager());

        return toDto(timeSheetRepo.save(ts));
    }


    @Override
    public TimeSheetDto approveSheet(Long id) {
        TimeSheet ts = timeSheetRepo.findById(id).orElseThrow(() -> new RuntimeException("Sheet not found"));
        ts.setStatus(SheetStatus.APPROVED);
        ts.setApprovedDate(new Date());
        return toDto(timeSheetRepo.save(ts));
    }

    @Override
    public TimeSheetDto rejectSheet(Long id, String comments) {
        TimeSheet ts = timeSheetRepo.findById(id).orElseThrow(() -> new RuntimeException("Sheet not found"));
        ts.setStatus(SheetStatus.REJECTED);
        ts.setComments(comments);
        return toDto(timeSheetRepo.save(ts));
    }

    @Override
    public TimeSheetDto resubmitSheet(Long id) {
        TimeSheet ts = timeSheetRepo.findById(id).orElseThrow(() -> new RuntimeException("Sheet not found"));
        ts.setStatus(SheetStatus.REVISED);
        return toDto(timeSheetRepo.save(ts));
    }

    @Override
    public List<TimeSheetDto> getMySheets() {
        User user = getLoggedInUser();
        return timeSheetRepo.findByUserId(user.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TimeSheetDto> getPendingSheets() {
        User manager = getLoggedInUser();
        return timeSheetRepo.findByUserManagerIdAndStatus(manager.getId(), SheetStatus.PENDING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public TimeSheetDto getSheetById(Long id) {
        return toDto(timeSheetRepo.findById(id).orElseThrow(() -> new RuntimeException("Sheet not found")));
    }

    @Override
    public List<TimeSheetDto> getAllSheets() {
        return timeSheetRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }


    @Override
    public List<TimeSheetDto> getAllDraftSheetByUserID() {
        User user = getLoggedInUser();  // You already have this method
        return timeSheetRepo.findByUserIdAndStatus(user.getId(), SheetStatus.DRAFT)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSheetDto> getAllRejectSheetByUserId() {
        User user = getLoggedInUser();
        return timeSheetRepo.findByUserIdAndStatus(user.getId(), SheetStatus.REJECTED)
                .stream().map(this::toDto)
                .collect(Collectors.toList());

    }


    @Override
    public Map<UserDto, List<TimeSheetDto>> getTeamTimesheets() {
        User loggedInUser = getLoggedInUser();
        Role role = loggedInUser.getRole();
        Map<UserDto, List<TimeSheetDto>> result = new HashMap<>();

        if (role == Role.ADMIN) {
            List<User> allUsers = userRepo.findAll();
            for (User user : allUsers) {
                List<TimeSheetDto> sheets = timeSheetRepo.findByUserId(user.getId())
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
                result.put(userMapper.toDto(user), sheets);  // ✅ Correct usage
            }
        } else if (role == Role.MANAGER) {
            List<User> teamMembers = userRepo.findByManagerId(loggedInUser.getId());
            for (User user : teamMembers) {
                List<TimeSheetDto> sheets = timeSheetRepo.findByUserId(user.getId())
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
                result.put(userMapper.toDto(user), sheets);  // ✅ Correct usage
            }
        } else {
            throw new RuntimeException("Access Denied: Only Admin or Manager can access this.");
        }

        return result;
    }

    @Override
    public List<EmployeeTimesheetDto> getAllEmployeesWithTimesheets() {
        List<User> employees = userRepo.findAll();

        return employees.stream()
                .filter(user -> user.getRole().name().equalsIgnoreCase("EMPLOYEE"))
                .map(user -> {
                    List<TimeSheetDto> timesheetDtos = user.getTimesheets() != null
                            ? user.getTimesheets().stream().map(this::toDto).toList()
                            : List.of();
                    return EmployeeTimesheetDto.builder()
                            .employeeId(user.getId())
                            .employeeName(user.getName())
                            .email(user.getEmail())
                            .timesheets(timesheetDtos)
                            .build();
                })
                .toList();
    }



}