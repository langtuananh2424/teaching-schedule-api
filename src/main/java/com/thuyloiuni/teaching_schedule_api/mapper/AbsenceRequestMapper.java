package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AbsenceRequestMapper {

    @Mappings({
        // ---- Thông tin chung của đơn ----
        @Mapping(source = "requestId", target = "id"),
        @Mapping(source = "reason", target = "reason"),
        @Mapping(source = "createdAt", target = "createdAt"),
        @Mapping(source = "approvalStatus", target = "status"),
        @Mapping(source = "approver.fullName", target = "approverName"),

        // ---- Thông tin giảng viên xin nghỉ ----
        @Mapping(source = "lecturer.fullName", target = "lecturerName"),

        // ---- Thông tin buổi học gốc xin nghỉ (lấy từ Schedule -> Assignment -> ...) ----
        @Mapping(source = "schedule.assignment.subject.subjectName", target = "subjectName"),
        @Mapping(source = "schedule.assignment.studentClass.className", target = "className"),
        @Mapping(source = "schedule.sessionDate", target = "sessionDate"),
        @Mapping(source = "schedule.startPeriod", target = "startPeriod"),
        @Mapping(source = "schedule.endPeriod", target = "endPeriod"),
        @Mapping(source = "schedule.classroom", target = "classroom")

        // Ghi chú: Phần thông tin dạy bù sẽ được xử lý ở tầng Service
    })
    AbsenceRequestDTO toDto(AbsenceRequest absenceRequest);

    List<AbsenceRequestDTO> toDtoList(List<AbsenceRequest> absenceRequests);
}
