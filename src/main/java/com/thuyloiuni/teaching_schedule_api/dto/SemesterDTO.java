package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SemesterDTO {
    private Integer semesterId;
    private String name;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
}
