package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DepartmentDTO {
    private Integer departmentId;
    private String departmentName;
}
