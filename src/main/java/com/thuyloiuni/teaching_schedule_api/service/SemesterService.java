package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.SemesterDTO;

import java.util.List;

public interface SemesterService {
    List<String> getAcademicYears();
    List<SemesterDTO> getSemestersByAcademicYear(String academicYear);
}
