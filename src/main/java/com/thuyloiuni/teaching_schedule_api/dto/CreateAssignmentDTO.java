package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAssignmentDTO {
    @NotNull(message = "ID Học kỳ không được để trống")
    private Integer semesterId;

    @NotNull(message = "ID Môn học không được để trống")
    private Integer subjectId;

    @NotNull(message = "ID Lớp học không được để trống")
    private Integer classId;

    @NotNull(message = "ID Giảng viên không được để trống")
    private Integer lecturerId;
}
