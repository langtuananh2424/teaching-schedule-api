package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MakeupSessionMapper {
    // --- Mappings BẮT BUỘC vì tên trường khác nhau ---
    @Mapping(source = "id", target = "makeupSessionId")
    @Mapping(source = "managerApproval", target = "managerStatus")
    @Mapping(source = "academicAffairsApproval", target = "academicAffairsStatus")

    // --- Mappings BẮT BUỘC vì cần truy cập sâu vào các đối tượng lồng nhau ---
    @Mapping(source = "absentSchedule.sessionId", target = "absentSessionId")
    @Mapping(source = "absentSchedule.assignment.subject.subjectName", target = "subjectName")
    @Mapping(source = "absentSchedule.assignment.studentClass.className", target = "className")
    @Mapping(source = "absentSchedule.assignment.lecturer.fullName", target = "lecturerName")
    MakeupSessionDTO toDto(MakeupSession makeupSession);

    List<MakeupSessionDTO> toDtoList(List<MakeupSession> makeupSessions);
}
