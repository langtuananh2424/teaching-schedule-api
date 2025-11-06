package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.AbsenceRequestDTO;
import com.thuyloiuni.teaching_schedule_api.entity.AbsenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AbsenceRequestMapper {

    @Mapping(source = "requestId", target = "absenceRequestId")
    @Mapping(source = "managerApproval", target = "managerStatus")
    @Mapping(source = "academicAffairsApproval", target = "academicAffairsStatus")
    @Mapping(source = "lecturer.fullName", target = "lecturerName")
    @Mapping(source = "schedule.assignment.subject.subjectName", target = "subjectName")
    @Mapping(source = "schedule.assignment.studentClass.className", target = "className")
    @Mapping(source = "schedule.sessionDate", target = "sessionDate")
    @Mapping(source = "schedule.startPeriod", target = "startPeriod")
    @Mapping(source = "schedule.endPeriod", target = "endPeriod")
    @Mapping(source = "schedule.classroom", target = "classroom")
    AbsenceRequestDTO toDto(AbsenceRequest absenceRequest);

    List<AbsenceRequestDTO> toDtoList(List<AbsenceRequest> absenceRequests);
}
