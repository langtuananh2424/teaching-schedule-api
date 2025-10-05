package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStudentDTO {
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @NotBlank(message = "Tên sinh viên không được để trống")
    private String fullName;

    @NotNull(message = "ID lớp học không được để trống")
    private Integer classId;
}
