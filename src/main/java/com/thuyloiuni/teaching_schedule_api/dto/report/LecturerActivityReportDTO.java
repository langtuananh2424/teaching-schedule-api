package com.thuyloiuni.teaching_schedule_api.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerActivityReportDTO {

    // --- Basic Info ---
    private Integer assignmentId;
    private String lecturerName;
    private String subjectName;
    private String className;
    private String semesterName;
    private String academicYear;

    // --- Period Statistics ---
    private Periods plannedPeriods;
    private Periods taughtPeriods;

    // --- Student Attendance Details ---
    private List<StudentAttendanceReportDTO> studentAttendanceReports;

    /**
     * Inner class to represent period counts.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periods {
        private int total;
        private int theory;
        private int practice;
        private int regularTaught; // Taught in regular sessions
        private int makeupTaught;  // Taught in makeup sessions
    }
}
