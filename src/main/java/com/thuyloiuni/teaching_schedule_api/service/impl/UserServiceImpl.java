package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.dto.UpdateUserRequestDTO;
import com.thuyloiuni.teaching_schedule_api.dto.UserDTO;
import com.thuyloiuni.teaching_schedule_api.entity.User;
import com.thuyloiuni.teaching_schedule_api.exception.ResourceNotFoundException;
import com.thuyloiuni.teaching_schedule_api.mapper.UserMapper;
import com.thuyloiuni.teaching_schedule_api.repository.UserRepository;
import com.thuyloiuni.teaching_schedule_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UpdateUserRequestDTO userDetails) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (StringUtils.hasText(userDetails.getEmail()) && !existingUser.getEmail().equals(userDetails.getEmail())) {
            if(userRepository.findByEmail(userDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email '" + userDetails.getEmail() + "' đã được sử dụng.");
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        // Business Rule: Prevent deleting a user that is linked to a lecturer.
        // The lecturer must be deleted first, which will cascade and delete the user.
        if(userToDelete.getLecturer() != null) {
            throw new IllegalStateException("Không thể xóa người dùng này vì họ đã được liên kết với một giảng viên. Vui lòng xóa thông tin giảng viên trước.");
        }

        userRepository.delete(userToDelete);
    }
}
