package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.MakeupSessionDTO;
import com.thuyloiuni.teaching_schedule_api.entity.MakeupSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MakeupSessionMapper {

    // Ánh xạ các trường lồng nhau
    @Mapping(source = "absentRequest.sessionId", target = "absentSessionId")
    @Mapping(source = "approver.lecturerId", target = "approverId") // Có thể null
    @Mapping(source = "approver.fullName", target = "approverName") // Có thể null
    MakeupSessionDTO toDto(MakeupSession makeupSession);

    List<MakeupSessionDTO> toDtoList(List<MakeupSession> makeupSessions);
}
