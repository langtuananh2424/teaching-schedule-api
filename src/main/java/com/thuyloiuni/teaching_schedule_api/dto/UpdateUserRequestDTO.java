package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import lombok.Data;

@Data
public class UpdateUserRequestDTO {
    private String email;
    private RoleType role;
}
