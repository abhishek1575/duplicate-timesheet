package timesheetDuplicate.dto;
import lombok.Data;
@Data
public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String type = "Bearer";
    private String jwt;

    public AuthResponse(Long id, String name, String email, String role, String jwt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.jwt = jwt;

    }
}

