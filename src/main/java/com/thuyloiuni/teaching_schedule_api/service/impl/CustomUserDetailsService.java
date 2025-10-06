package com.thuyloiuni.teaching_schedule_api.service.impl;

import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LecturerRepository lecturerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm kiếm Lecturer trong DB bằng email (được dùng như username)
        Lecturer lecturer = lecturerRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        // 2. Tạo một danh sách quyền (authorities) từ vai trò (role) của Lecturer
        // Luôn thêm tiền tố "ROLE_" vào trước tên vai trò để tương thích với hasRole() của Spring Security.
        String roleName = "ROLE_" + lecturer.getRole().name(); // Ví dụ: "ADMIN" -> "ROLE_ADMIN"

        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(roleName));

        // 3. Trả về một đối tượng UserDetails mà Spring Security có thể sử dụng
        return new User(lecturer.getEmail(), lecturer.getPassword(), authorities);
    }
}
