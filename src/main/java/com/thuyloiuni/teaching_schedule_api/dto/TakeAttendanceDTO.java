package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TakeAttendanceDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private Integer studentId;

    @NotNull(message = "Trạng thái điểm danh không được để trống")
    private Boolean isPresent;
}
