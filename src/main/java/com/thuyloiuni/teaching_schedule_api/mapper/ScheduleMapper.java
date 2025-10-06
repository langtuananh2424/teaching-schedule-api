package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.ScheduleDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(source = "assignment.assignmentId", target = "assignmentId")
    @Mapping(source = "assignment.subject.subjectName", target = "subjectName")
    @Mapping(source = "assignment.lecturer.fullName", target = "lecturerName")
    @Mapping(source = "assignment.studentClass.className", target = "className")
    ScheduleDTO toDto(Schedule schedule);

    List<ScheduleDTO> toDtoList(List<Schedule> schedules);
}
