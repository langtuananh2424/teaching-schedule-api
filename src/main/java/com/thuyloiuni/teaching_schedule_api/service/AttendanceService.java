package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.AttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.dto.TakeAttendanceDTO;

import java.util.List;

public interface AttendanceService {
    List<AttendanceDTO> takeAttendance(TakeAttendanceDTO takeAttendanceDTO);
    List<AttendanceDTO> getAttendancesByScheduleId(Integer scheduleId);
}
