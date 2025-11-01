package com.thuyloiuni.teaching_schedule_api.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceReportDTO {
    private String studentCode;
    private String studentName;
    private int totalSessions;
    private int attendedSessions;
    private double absencePercentage;
}
