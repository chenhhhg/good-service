package bupt.goodservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one number")
    @Pattern(regexp = ".*[a-zA-Z].*", message = "Password must contain at least one letter")
    private String password;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\d{11}$", message = "Phone number must be 11 digits")
    private String phone;
}
