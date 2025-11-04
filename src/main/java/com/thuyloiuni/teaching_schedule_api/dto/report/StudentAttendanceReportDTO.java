package com.thuyloiuni.teaching_schedule_api.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class StudentAttendanceReportDTO {
    private String studentId;
    private String studentName;
    private String studentCode;
    private int totalSessions;
    private int attendedSessions;
    private int absentSessions;
    private List<AttendanceDetail> attendanceDetails;

    @Data
    @NoArgsConstructor
    public static class AttendanceDetail {
        private Integer sessionId;
        private LocalDateTime sessionDate;
        private boolean isPresent;
    }
}
