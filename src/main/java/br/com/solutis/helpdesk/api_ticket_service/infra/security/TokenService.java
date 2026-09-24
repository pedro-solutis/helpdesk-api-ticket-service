package br.com.solutis.helpdesk.api_ticket_service.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String jwtSecret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    public Long getUserId(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getClaim("id")
                    .asLong();
        } catch (JWTVerificationException e) {
            return 0L;
        }
    }

    public List<String> getUserRoles(String token) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        var role = JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .getClaim("role")
                .asString();
        return List.of(role);
    }
}

