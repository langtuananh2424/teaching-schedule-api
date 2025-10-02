package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDTO {
    private Integer id;
    private String lecturerCode;
    private String fullName;
    private String email;

    private Integer departmentId;
    private String departmentName;
    private RoleType role;
}