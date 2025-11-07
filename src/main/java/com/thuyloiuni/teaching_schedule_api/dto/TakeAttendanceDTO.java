package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TakeAttendanceDTO {

    @NotNull(message = "ID buổi học không được để trống")
    private Integer scheduleId;

    @NotEmpty(message = "Danh sách điểm danh không được để trống")
    @Valid // This ensures that the objects inside the list are also validated
    private List<StudentAttendanceStatusDTO> attendances;
}
