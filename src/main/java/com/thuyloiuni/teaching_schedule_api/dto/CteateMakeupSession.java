package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateMakeupSessionDTO {
    @NotNull(message = "ID của buổi học đã nghỉ không được để trống")
    private Long absentSessionId;

    @NotNull(message = "Ngày dạy bù không được để trống")
    @Future(message = "Ngày dạy bù phải là một ngày trong tương lai")
    private LocalDateTime makeupDate;

    @NotNull(message = "Tiết bắt đầu không được để trống")
    private Integer makeupStartPeriod;

    @NotNull(message = "Tiết kết thúc không được để trống")
    private Integer makeupEndPeriod;

    @NotBlank(message = "Phòng học không được để trống")
    private String makeupClassroom;
}
