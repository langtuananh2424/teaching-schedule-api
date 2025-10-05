package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AbsenceRequestMapper {

    // Ánh xạ các trường lồng nhau từ các đối tượng liên quan
    @Mapping(source = "schedule.sessionId", target = "sessionId")
    @Mapping(source = "lecturer.lecturerId", target = "lecturerId")
    @Mapping(source = "lecturer.fullName", target = "lecturerName")
    @Mapping(source = "approver.lecturerId", target = "approverId") // Có thể null
    @Mapping(source = "approver.fullName", target = "approverName") // Có thể null
    AbsenceRequestDTO toDto(AbsenceRequest absenceRequest);

    List<AbsenceRequestDTO> toDtoList(List<AbsenceRequest> absenceRequests);
}
