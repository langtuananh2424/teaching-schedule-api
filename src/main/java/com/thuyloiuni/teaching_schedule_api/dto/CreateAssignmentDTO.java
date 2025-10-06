package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAssignmentDTO {
    @NotNull(message = "ID Môn học không được để trống")
    private Integer subjectId;

    @NotNull(message = "ID Lớp học không được để trống")
    private Integer classId;

    @NotNull(message = "ID Giảng viên không được để trống")
    private Integer lecturerId;

    @NotNull(message = "Số tiết lý thuyết không được để trống")
    @Min(value = 0, message = "Số tiết lý thuyết không được âm")
    private Integer theorySession;

    @NotNull(message = "Số tiết thực hành không được để trống")
    @Min(value = 0, message = "Số tiết thực hành không được âm")
    private Integer practiceSession;
}
