package com.thuyloiuni.teaching_schedule_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAbsenceRequestDTO {

    // --- Thông tin bắt buộc cho đơn xin nghỉ ---
    @NotNull(message = "ID của buổi học không được để trống")
    private Integer sessionId;

    @NotNull(message = "ID của giảng viên không được để trống")
    private Integer lecturerId;

    @NotBlank(message = "Lý do xin nghỉ không được để trống")
    private String reason;

    // --- Thông tin tùy chọn cho việc đề xuất dạy bù ngay lập tức ---
    // Các trường này có thể để trống (null) nếu chỉ xin nghỉ mà không đăng ký dạy bù ngay
    private LocalDateTime makeupDate;
    private Integer makeupStartPeriod;
    private Integer makeupEndPeriod;
    private String makeupClassroom;
}
