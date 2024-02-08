package com.dynns.cloudtecnologia.websocket.handler;

import com.dynns.cloudtecnologia.websocket.rest.dto.MensagemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Log LOG = LogFactory.getLog(WebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //após conexão estabelecida
        LOG.info("[afterConnectionEstablished] (Conexão estabelecida) Session id: " + session.getId());

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    //session.sendMessage(new TextMessage("Você ainda está conectado: " + LocalDateTime.now().toString()));

                    MensagemDTO msgDto = MensagemDTO.builder()
                            .idSession(session.getId())
                            .msg("Você está conectado: " + LocalDate.now().toString())
                            .build();

                    ObjectMapper objectMapper = new ObjectMapper();
                    // Converta o objeto MensagemDTO em uma string JSON
                    String mensagemJson = objectMapper.writeValueAsString(msgDto);

                    // Envie a mensagem como um objeto JSON
                    session.sendMessage(new TextMessage(mensagemJson));

                    //session.sendMessage(msgDto);

                } catch (IOException e) {
                    LOG.info("*** Erro ao enviar msg WebSocket ***");
                }
            }
        }, 10000L, 10000L);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //Ao receber a mensagem
        LOG.info("[handleTextMessage] (Mensagem Recebida:) Message id: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //após conexão encerrada
        LOG.info("[afterConnectionClosed] (Conexão encerrada) Session id: " + session.getId());
    }
}
