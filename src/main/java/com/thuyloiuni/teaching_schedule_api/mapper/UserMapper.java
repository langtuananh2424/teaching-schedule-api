package com.thuyloiuni.teaching_schedule_api.mapper;

import com.thuyloiuni.teaching_schedule_api.dto.UserDTO;
import com.thuyloiuni.teaching_schedule_api.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);
}
