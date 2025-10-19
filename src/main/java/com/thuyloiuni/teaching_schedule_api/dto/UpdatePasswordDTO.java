package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdatePasswordDTO {
    
    @NotEmpty(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;

    @NotEmpty(message = "Mật khẩu mới không được để trống")
    private String newPassword;
}
