package com.dynns.cloudtecnologia.websocket.util;

import org.springframework.stereotype.Component;


@Component
public abstract class JwtUtil {

    private JwtUtil() {
    }


    public static boolean tokenJwtIsValid(String token) {
        return true;
    }

}
