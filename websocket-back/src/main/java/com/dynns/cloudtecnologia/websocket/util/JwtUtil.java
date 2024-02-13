package com.dynns.cloudtecnologia.websocket.util;

import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

    private JwtUtil() {
    }


    public static boolean validarTokenJwt(String token) {
        return true;
    }

}
