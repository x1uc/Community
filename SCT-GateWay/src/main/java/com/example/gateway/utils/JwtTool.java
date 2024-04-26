package com.example.gateway.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtTool {
    private static final long EXPIRATION = 1000 * 60 * 60 * 100;
    private static final String SECRET_KEY = "ASSadasaafgdttyrtcuixMLSKA";


    public static String createToken(Long userId) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION);
        Map<String, String> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        String token = JWT.create()
                .withClaim("userId", userId)
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET_KEY));
        return token;
    }


    public static Long verifyToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            log.error("解析JWT错误");
            return null;
        }
        return jwt.getClaim("userId").asLong();
    }


}
