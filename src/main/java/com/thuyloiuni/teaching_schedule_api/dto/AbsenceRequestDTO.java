package com.thuyloiuni.teaching_schedule_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO này chứa thông tin đầy đủ về một đơn xin nghỉ phép,
 * kết hợp thông tin từ buổi học gốc và đề xuất dạy bù (nếu có).
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Các trường null sẽ không được trả về trong JSON
public class AbsenceRequestDTO {

    // ---- Thông tin chung của đơn ----
    private Integer id;
    private final String requestType = "Xin nghỉ dạy"; // Loại đề xuất
    private String reason;                             // Lý do xin nghỉ
    private LocalDateTime createdAt;                   // Thời gian tạo đơn
    private ApprovalStatus status;                     // Trạng thái duyệt
    private String approverName;                       // Tên người duyệt

    // ---- Thông tin giảng viên xin nghỉ ----
    private String lecturerName;                       // Tên giảng viên

    // ---- Thông tin buổi học gốc xin nghỉ ----
    private String subjectName;                        // Tên môn học
    private String className;                          // Tên lớp học
    private LocalDate sessionDate;                     // Ngày học
    private Integer startPeriod;                       // Tiết bắt đầu
    private Integer endPeriod;                         // Tiết kết thúc
    private String classroom;                          // Phòng học gốc

    // ---- Thông tin buổi dạy bù được đề xuất (sẽ là null nếu chưa có) ----
    private LocalDateTime makeupCreatedAt;             // Thời gian đăng ký dạy bù
    private LocalDate makeupDate;                      // Ngày đề xuất dạy bù
    private Integer makeupStartPeriod;                 // Tiết bắt đầu đề xuất
    private Integer makeupEndPeriod;                   // Tiết kết thúc đề xuất
    private String makeupClassroom;                    // Phòng học đề xuất
}
