package com.thuyloiuni.teaching_schedule_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Rất quan trọng để @PreAuthorize hoạt động
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
                        // ==========================================================
                        // =====             CÁC ENDPOINT CÔNG KHAI             =====
                        // ==========================================================
                        .requestMatchers("/api/auth/login").permitAll() // Endpoint đăng nhập
                        .requestMatchers(HttpMethod.POST, "/api/lecturers").permitAll() // Endpoint đăng ký tài khoản giảng viên

                        // ==========================================================
                        // =====           QUYỀN CHUNG CHO NGƯỜI DÙNG          =====
                        // ==========================================================
                        // Yêu cầu người dùng phải xác thực (đăng nhập) để đọc thông tin.
                        // Áp dụng cho cả LECTURER và ADMIN.
                        .requestMatchers(HttpMethod.GET,
                                "/api/lecturers/**",      // Xem thông tin giảng viên
                                "/api/subjects/**",       // Xem thông tin môn học
                                "/api/departments/**",    // Xem thông tin khoa
                                "/api/student-classes/**",// Xem thông tin lớp học
                                "/api/students/**",       // Xem thông tin sinh viên
                                "/api/assignments/**",    // Xem thông tin phân công
                                "/api/schedules/**",      // Xem lịch học chi tiết
                                "/api/attendance/**",     // Xem kết quả điểm danh
                                "/api/absence-requests/**",// Xem đơn xin nghỉ
                                "/api/makeup-sessions/**" // Xem lịch dạy bù
                        ).authenticated()

                        // ==========================================================
                        // =====            QUYỀN RIÊNG CỦA LECTURER           =====
                        // ==========================================================
                        // Các hành động mà chỉ giảng viên mới thực hiện
                        .requestMatchers(HttpMethod.POST,
                                "/api/attendance/**",      // Giảng viên thực hiện điểm danh
                                "/api/absence-requests",   // Giảng viên tạo đơn xin nghỉ
                                "/api/makeup-sessions"     // Giảng viên đăng ký dạy bù
                        ).hasRole("LECTURER")

                        // ==========================================================
                        // =====        QUYỀN QUẢN TRỊ VIÊN (ADMIN)            =====
                        // ==========================================================
                        // Các hành động quản lý, chỉ ADMIN mới có quyền
                        .requestMatchers(
                                "/api/lecturers/**",       // Quản lý giảng viên (PUT, DELETE)
                                "/api/subjects/**",        // Quản lý môn học (POST, PUT, DELETE)
                                "/api/departments/**",     // Quản lý khoa (POST, PUT, DELETE)
                                "/api/student-classes/**", // Quản lý lớp học (POST, PUT, DELETE)
                                "/api/students/**",        // Quản lý sinh viên (POST, PUT, DELETE)
                                "/api/assignments/**",     // Quản lý phân công (POST, PUT, DELETE)
                                "/api/schedules/**",       // Quản lý lịch học (POST, PUT, DELETE)
                                "/api/absence-requests/**",// Duyệt đơn xin nghỉ (PATCH)
                                "/api/makeup-sessions/**"  // Duyệt lịch dạy bù (PATCH)
                        ).hasRole("ADMIN")

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
