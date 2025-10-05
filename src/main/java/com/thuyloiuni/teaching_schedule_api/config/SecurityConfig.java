package com.thuyloiuni.teaching_schedule_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <<== Import HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
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
                // CHO PHÉP CÁC ENDPOINT CÔNG KHAI
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll() // Endpoint đăng nhập
                .requestMatchers(HttpMethod.POST, "/api/lecturers").permitAll() // Cho phép tạo giảng viên mới mà không cần xác thực
                .requestMatchers(HttpMethod.DELETE, "/api/lecturers").permitAll()


                // YÊU CẦU XÁC THỰC CHO TẤT CẢ CÁC REQUEST CÒN LẠI
                .anyRequest().authenticated()
            );
        
        // Nếu bạn có JWT, bạn sẽ thêm bộ lọc JWT vào đây
        // http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
