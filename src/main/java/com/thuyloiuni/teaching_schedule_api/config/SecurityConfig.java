package com.thuyloiuni.teaching_schedule_api.config;

import com.thuyloiuni.teaching_schedule_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Giữ lại để phòng khi cần dùng trong tương lai
@RequiredArgsConstructor // Sử dụng constructor injection
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Inject JwtAuthenticationFilter

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // =================================================================
                        // ===== 1. CÁC ENDPOINT CÔNG KHAI (KHÔNG CẦN XÁC THỰC)         =====
                        // =================================================================
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/lecturers").permitAll()

                        // =================================================================
                        // ===== 2. QUYỀN RIÊNG CỦA ADMIN (Hành động ghi/sửa/xóa)      =====
                        // =================================================================
                        // Các quy tắc này cụ thể nhất (method + role), đặt lên trên
                        .requestMatchers(HttpMethod.PUT, "/api/lecturers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/lecturers/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/subjects/**", "/api/departments/**", "/api/student-classes/**", "/api/students/**", "/api/assignments/**", "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/subjects/**", "/api/departments/**", "/api/student-classes/**", "/api/students/**", "/api/assignments/**", "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/subjects/**", "/api/departments/**", "/api/student-classes/**", "/api/students/**", "/api/assignments/**", "/api/schedules/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/absence-requests/**", "/api/makeup-sessions/**").hasRole("ADMIN") // Duyệt đơn

                        // =================================================================
                        // ===== 3. QUYỀN RIÊNG CỦA LECTURER (Hành động tạo)           =====
                        // =================================================================
                        .requestMatchers(HttpMethod.POST,
                                "/api/attendance/**",      // Giảng viên thực hiện điểm danh
                                "/api/absence-requests",   // Giảng viên tạo đơn xin nghỉ
                                "/api/makeup-sessions"     // Giảng viên đăng ký dạy bù
                        ).hasRole("LECTURER")

                        // =================================================================
                        // ===== 4. QUYỀN CHUNG (CHỈ CẦN ĐĂNG NHẬP) - Đa số là GET      =====
                        // =================================================================
                        // Vì các quyền ghi/sửa/xóa đã được xử lý ở trên, các request còn lại (chủ yếu là GET) sẽ rơi vào đây.
                        .requestMatchers(
                                "/api/lecturers/**",
                                "/api/subjects/**",
                                "/api/departments/**",
                                "/api/student-classes/**",
                                "/api/students/**",
                                "/api/assignments/**",
                                "/api/schedules/**",
                                "/api/attendance/**",
                                "/api/absence-requests/**",
                                "/api/makeup-sessions/**"
                        ).authenticated()

                        // =================================================================
                        // ===== 5. QUY TẮC CUỐI CÙNG (FALLBACK)                         =====
                        // =================================================================
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        // KÍCH HOẠT BỘ LỌC JWT - BẮT BUỘC PHẢI CÓ
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
