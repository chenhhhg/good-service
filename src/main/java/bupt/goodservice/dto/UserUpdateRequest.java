package bupt.goodservice.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be 11 digits")
    private String phone;

    private String profile;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one number")
    @Pattern(regexp = ".*[a-zA-Z].*", message = "Password must contain at least one letter")
    private String password;
}
