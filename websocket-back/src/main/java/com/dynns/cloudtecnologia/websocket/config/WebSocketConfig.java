package com.dynns.cloudtecnologia.websocket.config;

import com.dynns.cloudtecnologia.websocket.interceptor.AuthorizationHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

    /**
     * @param registry Configurando a URL de conexão com o WebSocket
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // URL RAIZ do websocket:  ws://localhost:8080/chat
        registry.addHandler(webSocketHandler, "/chat")
                .addInterceptors(new AuthorizationHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
