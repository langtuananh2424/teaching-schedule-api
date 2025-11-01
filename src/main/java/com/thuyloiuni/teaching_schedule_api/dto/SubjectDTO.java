package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {

    private Integer id;
    private String subjectCode;
    private String subjectName;
    private int credits;

    // Fields for reporting planned periods
    private int theoryPeriods;
    private int practicePeriods;

    // Department Info
    private Integer departmentId;
    private String departmentName;
}
