package com.example.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class JWTAuthenticator {
    public static String generateJWT (String username, String password) {
        String token = null;
        JSONObject configObject = new JSONObject(GetJson.from("./config.json"));

        try {
            if (configObject.getString("username").equals(username)
                    && configObject.getString("password").equals(password))
            {
                String key = configObject.getString("secret-key");

                Algorithm algorithm = Algorithm.HMAC256(key);
                LocalDateTime dateTime = LocalDateTime.now().plusHours(1);
                Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

                token = JWT.create()
                        .withIssuer(username)
                        .withExpiresAt(date)
                        .sign(algorithm);
            }
        } catch (JWTCreationException e) {
            e.printStackTrace();
        }

        return token;
    }

    public static boolean validateJWT (String token) {
        if (token == null) return false;

        JSONObject configObject = new JSONObject(GetJson.from("./config.json"));
        String key = configObject.getString("secret-key");
        Algorithm algorithm = Algorithm.HMAC256(key);

        JWTVerifier verifier = JWT.require(algorithm).withIssuer(configObject.getString("username")).acceptExpiresAt(5).build();

        try {
            DecodedJWT jwt = verifier.verify(token);
            System.out.println(jwt.getPayload());
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private static String randomKey(int count) {
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
