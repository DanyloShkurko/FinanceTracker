package org.example.cloudgateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token.replace("Bearer ",""))
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return this.extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isInvalid(String token) {
        try {
            return isAuthMissing(token) || this.isTokenExpired(token);
        } catch (Exception e) {
            return true;
        }
    }

    private boolean isAuthMissing(String token) {
        return token == null || !token.startsWith("Bearer ");
    }
}