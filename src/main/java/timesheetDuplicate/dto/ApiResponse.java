package timesheetDuplicate.dto;

import lombok.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private HttpStatus errorCode;
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(String message, boolean success) {
        this.timestamp = LocalDateTime.now();
        this.status = success ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value();
        this.errorCode = success ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .errorCode(HttpStatus.OK)
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorCode(status)
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}