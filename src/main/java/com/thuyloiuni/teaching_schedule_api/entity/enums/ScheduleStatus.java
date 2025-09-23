package com.thuyloiuni.teaching_schedule_api.entity.enums;

public enum ScheduleStatus {
    SCHEDULED,    // Đã lên lịch
    TAUGHT,       // Đã dạy
    CANCELLED,    // Đã hủy (bởi giảng viên hoặc phòng đào tạo)
    MAKEUP_SCHEDULED, // Đã xếp lịch bù
    COMPLETED_MAKEUP  // Đã hoàn thành buổi học bù
}
