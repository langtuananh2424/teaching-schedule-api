package com.thuyloiuni.teaching_schedule_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Bật một message broker đơn giản, đích đến cho các tin nhắn bắt đầu bằng "/topic"
        config.enableSimpleBroker("/topic");
        // Tiền tố cho các tin nhắn được ánh xạ tới các phương thức @MessageMapping trong controller
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint "/ws" để client kết nối tới.
        // withSockJS() là để dự phòng khi trình duyệt không hỗ trợ WebSocket.
        registry.addEndpoint("/ws").withSockJS();
    }
}
