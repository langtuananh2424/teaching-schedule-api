package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.SemesterDTO;
import com.thuyloiuni.teaching_schedule_api.mapper.SemesterMapper;
import com.thuyloiuni.teaching_schedule_api.repository.SemesterRepository;
import com.thuyloiuni.teaching_schedule_api.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final SemesterMapper semesterMapper;

    @Override
    public List<String> getAcademicYears() {
        return semesterRepository.findDistinctAcademicYears();
    }

    @Override
    public List<SemesterDTO> getSemestersByAcademicYear(String academicYear) {
        return semesterRepository.findByAcademicYear(academicYear)
                .stream()
                .map(semesterMapper::toDto)
                .collect(Collectors.toList());
    }
}
