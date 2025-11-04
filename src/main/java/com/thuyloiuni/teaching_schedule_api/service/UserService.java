package com.thuyloiuni.teaching_schedule_api.service;

import com.thuyloiuni.teaching_schedule_api.dto.UpdateUserRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long userId);

    UserDTO updateUser(Long userId, UpdateUserRequestDTO userDetails);

    void deleteUser(Long userId);
}
