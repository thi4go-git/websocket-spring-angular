package com.dynns.cloudtecnologia.websocket.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dynns.cloudtecnologia.websocket.exception.GeralException;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public abstract class JwtUtil {

    private JwtUtil() {
    }

    private static final String ISSUER_REALM = "http://cloudtecnologia.dynns.com:8180/realms/CLOUD_TECNOLOGIA";
    private static final String CLIENT_ID = "fluxo-caixa-client";


    public static boolean tokenJwtIsValid(String token) {
        token = token.replace("Bearer ", "");
        DecodedJWT jwt = JWT.decode(token);

        boolean tokenExpirado = jwt
                .getExpiresAtAsInstant()
                .atZone(ZoneId.systemDefault())
                .isBefore(ZonedDateTime.now());

        if (tokenExpirado) {
            throw new GeralException("Token expirado, data token: " + jwt.getExpiresAt());
        }

        String realmIssuer = jwt.getIssuer();
        if (!realmIssuer.equals(ISSUER_REALM)) {
            throw new GeralException("Realm issuer é inválido " + realmIssuer);
        }

        String clientId = jwt.getClaim("azp").asString();
        if (!clientId.equals(CLIENT_ID)) {
            throw new GeralException("Client id inválido: " + clientId);
        }

        return true;
    }

}
