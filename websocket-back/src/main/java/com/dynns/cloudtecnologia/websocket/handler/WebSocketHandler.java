package com.dynns.cloudtecnologia.websocket.handler;

import com.dynns.cloudtecnologia.websocket.exception.GeralException;
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
        Optional<String> permissaoOpt = obterOptionalParametroPermissaoUrl(session);

        if (permissaoOpt.isEmpty() || permissaoOpt.get().isBlank()) {
            LOG.warning("Conexão negada: Não contém o parâmetro 'conecta' na URL.");
            encerrarConexaoWebSocket(session, CloseStatus.POLICY_VIOLATION);
            return;
        }

        if (!permissaoOpt.get().equals("true")) {
            LOG.warning("Conexão negada, (parâmetro conecta deve ser true) parâmetro conecta: " + permissaoOpt.get());
            encerrarConexaoWebSocket(session, CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        sessionsStatic.add(session);
        LOG.info("[afterConnectionEstablished] (Conexão estabelecida) Session id: " + session.getId());

        MensagemDTO msgDto = MensagemDTO.builder()
                .idSession(session.getId())
                .msg("Conexão estabelecida: " + session.getId() + " - " + LocalDateTime.now())
                .build();

        enviarMensagem(session, msgDto);
    }

    private Optional<String> obterOptionalParametroPermissaoUrl(WebSocketSession session) {
        // Esse método espera que seja passado um valor como parâmetro na URL:
        //ws://localhost:8080/chat?conecta=true
        return Optional
                .ofNullable(session.getUri())
                .map(UriComponentsBuilder::fromUri)
                .map(UriComponentsBuilder::build)
                .map(UriComponents::getQueryParams)
                .map(it -> it.get("conecta"))
                .flatMap(it -> it.stream().findFirst())
                .map(String::trim);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LOG.info("[handleTextMessage] (Mensagem Recebida:) Message id: " + message.getPayload());
        MensagemDTO msgDto = MensagemDTO.builder()
                .idSession(session.getId())
                .msg("[handleTextMessage] (Mensagem Recebida):  " + message.getPayload())
                .build();
        enviarMensagem(session, msgDto);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionsStatic.remove(session);
        LOG.info("[afterConnectionClosed] (Conexão encerrada) Session id: " + session.getId());
    }

    private void encerrarConexaoWebSocket(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException e) {
            throw new GeralException("Erro ao encerrar conexão!");
        }
    }

    public static void notifyAllClients(MensagemDTO msgDto) {
        for (WebSocketSession session : sessionsStatic) {
            enviarMensagem(session, msgDto);
        }
    }

    private static void enviarMensagem(WebSocketSession session, MensagemDTO msgDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        String mensagemJson;
        try {
            mensagemJson = objectMapper.writeValueAsString(msgDto);
        } catch (JsonProcessingException e) {
            throw new GeralException("Erro ao converter MensagemDTO para JSON!");
        }

        try {
            session.sendMessage(new TextMessage(mensagemJson));
            LOG.info("::: MSG Enviada ::: ");
            LOG.info(mensagemJson);
        } catch (IOException e) {
            throw new GeralException("Erro ao enviar MSG WebSocket!");
        }

    }
}
