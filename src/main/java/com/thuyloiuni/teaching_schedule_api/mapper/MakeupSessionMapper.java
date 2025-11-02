package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MakeupSessionMapper {

    @Mapping(source = "makeupSessionId", target = "makeupSessionId")
    @Mapping(source = "absentSchedule.sessionId", target = "absentSessionId")
    @Mapping(source = "makeupDate", target = "makeupDate")
    @Mapping(source = "makeupStartPeriod", target = "makeupStartPeriod")
    @Mapping(source = "makeupEndPeriod", target = "makeupEndPeriod")
    @Mapping(source = "makeupClassroom", target = "makeupClassroom")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "managerApproval", target = "managerStatus")
    @Mapping(source = "academicAffairsApproval", target = "academicAffairsStatus")
    MakeupSessionDTO toDto(MakeupSession makeupSession);

    List<MakeupSessionDTO> toDtoList(List<MakeupSession> makeupSessions);
}
