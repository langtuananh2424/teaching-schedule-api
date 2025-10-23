package com.thuyloiuni.teaching_schedule_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // Áp dụng cho tất cả các đường dẫn dưới /api/
                        .allowedOrigins("*")       // Cho phép tất cả các nguồn (thay "*" bằng URL của frontend khi triển khai thực tế)
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Các phương thức được phép
                        .allowedHeaders("*")       // Cho phép tất cả các header
                        .allowCredentials(false);    // Không cho phép gửi cookie và thông tin xác thực qua CORS trong cấu hình này
            }
        };
    }
}
