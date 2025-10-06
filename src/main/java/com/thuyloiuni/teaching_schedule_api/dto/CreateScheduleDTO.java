package com.thuyloiuni.teaching_schedule_api.dto;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateScheduleDTO {
    @NotNull(message = "ID Phân công không được để trống")
    private Integer assignmentId;

    @NotNull(message = "Ngày học không được để trống")
    @FutureOrPresent(message = "Ngày học phải là ngày hiện tại hoặc trong tương lai")
    private LocalDateTime sessionDate;

    @NotNull(message = "Thứ tự buổi học không được để trống")
    private Integer lessonOrder;

    @NotNull(message = "Tiết bắt đầu không được để trống")
    private Integer startPeriod;

    @NotNull(message = "Tiết kết thúc không được để trống")
    private Integer endPeriod;

    @NotBlank(message = "Phòng học không được để trống")
    private String classroom;

    private String content;
    private String notes;

    // Khi tạo mới, có thể không cần gửi lên, service sẽ tự gán PENDING
    // Khi cập nhật, có thể gửi lên trạng thái mới
    private ScheduleStatus status = ScheduleStatus.NOT_TAUGHT;
}
