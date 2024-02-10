package com.dynns.cloudtecnologia.websocket.service;

import com.dynns.cloudtecnologia.websocket.handler.WebSocketHandler;
import com.dynns.cloudtecnologia.websocket.rest.dto.MensagemDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class MsgAutomatica {

    private static final Logger LOG = Logger.getLogger(MsgAutomatica.class.getName());

    @Bean
    public CommandLineRunner executarAutomatico() {
        return args -> {
            enviarMsgAutomatica();
        };
    }

    private void enviarMsgAutomatica() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MensagemDTO msgAuto = MensagemDTO.builder()
                        .idSession(UUID.randomUUID().toString())
                        .msg("Mensagem automática BACKEND: " + LocalDateTime.now())
                        .build();
                WebSocketHandler.notifyAllClients(msgAuto);
                LOG.info("::: MSG AUTOMATICA ENVIADA ! :::");
            }
        }, 10000L, 10000L);
    }

}
