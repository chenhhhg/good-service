package bupt.goodservice.model;

import bupt.goodservice.model.enums.UserType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private UserType userType;
    private String name;
    private String phone;
    private String profile;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
