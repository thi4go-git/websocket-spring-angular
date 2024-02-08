package com.dynns.cloudtecnologia.websocket.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MensagemDTO {
    private String idSession;
    private String msg;
}
