package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.AttendanceDTO;
import com.thuyloiuni.teaching_schedule_api.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(source = "attendanceId", target = "attendanceId") // Sửa từ id -> attendanceId
    @Mapping(source = "schedule.sessionId", target = "sessionId")
    @Mapping(source = "student.studentId", target = "studentId")
    @Mapping(source = "student.studentCode", target = "studentCode")
    @Mapping(source = "student.fullName", target = "studentFullName")
    @Mapping(source = "isPresent", target = "isPresent")
    AttendanceDTO toDto(Attendance attendance);

    List<AttendanceDTO> toDtoList(List<Attendance> attendances);
}
