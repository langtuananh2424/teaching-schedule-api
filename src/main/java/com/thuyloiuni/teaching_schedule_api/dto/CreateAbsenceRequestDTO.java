package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAbsenceRequestDTO {
    @NotNull(message = "ID của buổi học không được để trống")
    private Integer sessionId;

    @NotNull(message = "ID của giảng viên không được để trống")
    private Integer lecturerId;

    @NotBlank(message = "Lý do xin nghỉ không được để trống")
    private String reason;
}
