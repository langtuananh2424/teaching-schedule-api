package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String email;
    private RoleType role;
}
