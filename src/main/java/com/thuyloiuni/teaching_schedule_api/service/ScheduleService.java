package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.CreateScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.dto.ScheduleDTO;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> getAllSchedules();
    ScheduleDTO getScheduleById(Integer id);
    List<ScheduleDTO> getSchedulesByAssignment(Integer assignmentId);
    List<ScheduleDTO> getSchedulesByLecturerEmail(String email);
    ScheduleDTO createSchedule(CreateScheduleDTO createDto);
    ScheduleDTO updateSchedule(Integer id, CreateScheduleDTO updateDto);
    void deleteSchedule(Integer id);
}
