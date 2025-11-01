package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import lombok.Data;

@Data
public class LecturerDTO {
    private Integer lecturerId;
    private String lecturerCode;
    private String fullName;
    private String email;
    private RoleType role;
    private Integer departmentId;
    private String departmentName;
}
