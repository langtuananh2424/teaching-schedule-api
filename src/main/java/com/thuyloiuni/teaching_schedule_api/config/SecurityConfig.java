package com.thuyloiuni.teaching_schedule_api.config;

import com.thuyloiuni.teaching_schedule_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // =================================================================
                        // ===== 1. CÁC ENDPOINT CÔNG KHAI (KHÔNG CẦN XÁC THỰC)         =====\n                        // =================================================================

                        // SỬA LỖI: Cho phép tất cả các request OPTIONS (preflight) của CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // SỬA LỖI: Cho phép kết nối WebSocket
                        .requestMatchers("/ws/**").permitAll()

                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/lecturers").permitAll()

                        // =================================================================
                        // ===== CÁC QUY TẮC KHÁC GIỮ NGUYÊN                             =====\n                        // =================================================================
                        .requestMatchers(HttpMethod.PUT, "/api/lecturers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/lecturers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/subjects/**", "/api/departments/**", "/api/student-classes/**", "/api/students/**", "/api/assignments/**", "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/subjects/**", "/api/departments/**", "/api/student-classes/**", "/api/students/**", "/api/assignments/**", "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/subjects/**", "/api/departments/**", "/api/student-classes/**", "/api/students/**", "/api/assignments/**", "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/absence-requests/**", "/api/makeup-sessions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/attendance/**", "/api/absence-requests", "/api/makeup-sessions").hasRole("LECTURER")
                        .requestMatchers("/api/lecturers/**", "/api/subjects/**", "/api/departments/**", "/api/student-classes/**", "/api/students/**", "/api/assignments/**", "/api/schedules/**", "/api/attendance/**", "/api/absence-requests/**", "/api/makeup-sessions/**").authenticated()
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}