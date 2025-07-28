package timesheetDuplicate.entity;



import org.springframework.stereotype.Component;
import timesheetDuplicate.dto.UserDto;


@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        if (user.getManager() != null) {
            dto.setManagerId(user.getManager().getId());
            dto.setManagerName(user.getManager().getName());
        }

        return dto;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        return user;
    }
}