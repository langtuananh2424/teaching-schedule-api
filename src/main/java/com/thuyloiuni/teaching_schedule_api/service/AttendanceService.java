package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.AttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.dto.TakeAttendanceDTO;

import java.util.List;

public interface AttendanceService {

    /**
     * Lấy danh sách điểm danh của một buổi học.
     * @param sessionId ID của buổi học.
     * @return Danh sách điểm danh.
     */
    List<AttendanceDTO> getAttendanceBySessionId(Integer sessionId);

    /**
     * Thực hiện hoặc cập nhật điểm danh cho một buổi học.
     * @param sessionId ID của buổi học.
     * @param attendanceList Danh sách thông tin điểm danh của sinh viên.
     * @return Danh sách điểm danh sau khi đã lưu.
     */
    List<AttendanceDTO> takeAttendance(Integer sessionId, List<TakeAttendanceDTO> attendanceList);
}
