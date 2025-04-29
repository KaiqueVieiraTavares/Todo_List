package com.example.demo.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.entities.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${token.secreto}")
    private String code;
    @Value("${issuer-code}")
    private String issuer;

    public String generateToken(UserEntity user) {
        Algorithm algorithm = Algorithm.HMAC256(code);
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())
                .withExpiresAt(Date.from(Instant.now().plus(2, ChronoUnit.HOURS))) // UTC
                .sign(algorithm);
    }

    public String verifyToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(code);
        return JWT.require(algorithm).withIssuer(issuer).build().verify(token).getSubject();
    }
}
