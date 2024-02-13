package com.dynns.cloudtecnologia.websocket.interceptor;

import com.dynns.cloudtecnologia.websocket.exception.GeralException;
import com.dynns.cloudtecnologia.websocket.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public class AuthorizationHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger LOG = Logger.getLogger(AuthorizationHandshakeInterceptor.class.getName());

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
            WebSocketHandler webSocketHandler, Map<String, Object> map
    ) {
        HttpHeaders headers = serverHttpRequest.getHeaders();
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new GeralException("Erro: Nenhum Header encontrado para " + HttpHeaders.AUTHORIZATION);
        }

        String token = requireNonNull(headers.get(HttpHeaders.AUTHORIZATION))
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Nenhum token jwt Bearer encontrado no " + HttpHeaders.AUTHORIZATION));

        token = token.replace("Bearer ", "");
        LOG.info("****** TOKEN JWT ******");
        LOG.info(token);

        return JwtUtil.tokenJwtIsValid(token);
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
            WebSocketHandler webSocketHandler, Exception e
    ) {
        // TODOs document why this method is empty
    }
}
