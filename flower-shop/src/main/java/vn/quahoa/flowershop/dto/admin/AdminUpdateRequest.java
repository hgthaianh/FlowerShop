package vn.quahoa.flowershop.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
