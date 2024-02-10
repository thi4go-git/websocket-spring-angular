package com.dynns.cloudtecnologia.websocket.handler;

import com.dynns.cloudtecnologia.websocket.rest.dto.MensagemDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;


@Getter
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger LOG = Logger.getLogger(WebSocketHandler.class.getName());
    private static final Set<WebSocketSession> sessionsStatic = new CopyOnWriteArraySet<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        //Ao realizar conexão

        Optional<String> permissaoOpt = obterOptionalParametroPermissaoUrl(session);

        if (permissaoOpt.isEmpty() || permissaoOpt.get().isBlank()) {
            LOG.warning("Conexão negada: Não contém o parâmetro 'permissao' na URL.");
            encerrarConexaoWebSocket(session, CloseStatus.POLICY_VIOLATION);
            return;
        }

        String permissao = permissaoOpt.get();
        if (!permissao.equals("true")) {
            LOG.warning("Conexão negada, permissão URL: " + permissao);
            encerrarConexaoWebSocket(session, CloseStatus.POLICY_VIOLATION);
            return;
        }

        MensagemDTO msgDto = MensagemDTO.builder()
                .idSession(session.getId())
                .msg("Conexão estabelecida: " + session.getId() + " - " + LocalDateTime.now())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String mensagemJson;
        try {
            mensagemJson = objectMapper.writeValueAsString(msgDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            session.sendMessage(new TextMessage(mensagemJson));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sessionsStatic.add(session);
        LOG.info("[afterConnectionEstablished] (Conexão estabelecida) Session id: " + session.getId());
    }

    private Optional<String> obterOptionalParametroPermissaoUrl(WebSocketSession session) {
        // Esse método espera que seja passado um valor como parâmetro na URL:
        //ws://localhost:8080/chat?permissao=true
        return Optional
                .ofNullable(session.getUri())
                .map(UriComponentsBuilder::fromUri)
                .map(UriComponentsBuilder::build)
                .map(UriComponents::getQueryParams)
                .map(it -> it.get("permissao"))
                .flatMap(it -> it.stream().findFirst())
                .map(String::trim);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //Ao receber a mensagem
        LOG.info("[handleTextMessage] (Mensagem Recebida:) Message id: " + message.getPayload());

        MensagemDTO msgDto = MensagemDTO.builder()
                .idSession(session.getId())
                .msg("[handleTextMessage]:  " + message.getPayload())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String mensagemJson = objectMapper.writeValueAsString(msgDto);
        session.sendMessage(new TextMessage(mensagemJson));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Ao encerrar a conexão
        MensagemDTO msgDto = MensagemDTO.builder()
                .idSession(session.getId())
                .msg("Conexão ENCERRADA: " + session.getId() + " - " + LocalDateTime.now())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String mensagemJson;
        try {
            mensagemJson = objectMapper.writeValueAsString(msgDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            session.sendMessage(new TextMessage(mensagemJson));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sessionsStatic.remove(session);
        LOG.info("[afterConnectionClosed] (Conexão encerrada) Session id: " + session.getId());
    }

    private void encerrarConexaoWebSocket(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void notifyAllClients(MensagemDTO msgDto) {
        for (WebSocketSession session : sessionsStatic) {
            ObjectMapper objectMapper = new ObjectMapper();
            String mensagemJson;
            try {
                mensagemJson = objectMapper.writeValueAsString(msgDto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            try {
                session.sendMessage(new TextMessage(mensagemJson));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
