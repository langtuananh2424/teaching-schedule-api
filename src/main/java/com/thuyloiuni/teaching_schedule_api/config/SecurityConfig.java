package com.thuyloiuni.teaching_schedule_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Đã có
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Giữ lại, rất quan trọng để @PreAuthorize hoạt động
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF cho API
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sử dụng stateless session
                .authorizeHttpRequests(authorize -> authorize
                        // ===== CÁC ENDPOINT CÔNG KHAI (PUBLIC) =====
                        // Ai cũng có thể truy cập các endpoint này.
                        .requestMatchers("/api/auth/login").permitAll() // Endpoint đăng nhập
                        .requestMatchers(HttpMethod.POST, "/api/lecturers").permitAll() // Endpoint đăng ký giảng viên mới

                        // ===== PHÂN QUYỀN CHO GIẢNG VIÊN (LECTURERS) =====
                        // Yêu cầu người dùng phải xác thực (đăng nhập) để đọc thông tin.
                        // Cả ADMIN và LECTURER đều có thể truy cập sau khi đăng nhập.
                        .requestMatchers(HttpMethod.GET, "/api/lecturers/**").authenticated() // Xem danh sách hoặc chi tiết giảng viên
                        .requestMatchers(HttpMethod.GET, "/api/subjects/**").authenticated() // Xem danh sách hoặc chi tiết môn học
                        // Thêm các quyền GET khác cho LECTURER ở đây...

                        // ===== PHÂN QUYỀN CHO QUẢN TRỊ VIÊN (ADMIN) =====
                        // Chỉ người dùng có vai trò 'ADMIN' mới có thể truy cập các endpoint này.
                        // Sử dụng .requestMatchers cho các quyền quản lý tổng quát
                        .requestMatchers("/api/lecturers/**").hasRole("ADMIN") // Bất kỳ phương thức nào khác (PUT, DELETE) trên /api/lecturers đều cần quyền ADMIN
                        .requestMatchers("/api/subjects/**").hasRole("ADMIN") // Bất kỳ phương thức nào khác (POST, PUT, DELETE) trên /api/subjects đều cần quyền ADMIN
                        // Thêm các quyền quản lý khác cho ADMIN ở đây...

                        // ===== QUY TẮC CUỐI CÙNG (FALLBACK) =====
                        // Bất kỳ request nào không khớp với các quy tắc trên đều sẽ bị từ chối.
                        // Điều này an toàn hơn là dùng .anyRequest().authenticated() vì nó buộc bạn phải định nghĩa quyền cho mọi endpoint.
                        .anyRequest().denyAll()
                );

        // Nếu bạn có JWT, bạn sẽ thêm bộ lọc JWT vào đây
        // http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
