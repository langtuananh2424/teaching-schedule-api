package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceStatusDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private Integer studentId;

    @NotNull(message = "Trạng thái có mặt không được để trống")
    private Boolean isPresent;
}
