package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;

import lombok.Data;

@Data
public class CreateLecturerRequestDTO {
    private String lecturerCode;
    private String fullName;
    private String email;
    private String password;
    private Integer departmentId;
    private RoleType role;
}
