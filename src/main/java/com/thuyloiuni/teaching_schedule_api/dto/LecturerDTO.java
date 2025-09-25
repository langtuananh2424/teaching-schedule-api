package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;

import lombok.Data;

@Data
public class LecturerDTO {
    private Integer lecuturerId;
    private String lecturerCode;
    private String fullName;
    private String email;

    private Integer departmentId;
    private String departmentName;
    private RoleType role;
}