package com.example.user_service.service;

import com.example.user_service.model.exception.TokenNotValidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final UserService userService;

    @Autowired
    public JwtService(@Lazy UserService userService) {
        this.userService = userService;
    }


    public String extractUsername(String token) {
        if (token == null || !token.startsWith("Bearer ") || isTokenExpired(token.replace("Bearer ", ""))) {
            throw new TokenNotValidException("Token is empty");
        }
        log.debug("Extracting username from token");
        return extractClaim(token.replace("Bearer ", ""), Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting claim from token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.debug("Extracting all claims from token");
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        log.debug("Decoding secret key for signing JWT");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired");
        boolean expired = extractExpiration(token).before(new Date());
        log.info("Token expiration status: {}", expired ? "Expired" : "Valid");
        return expired;
    }

    private Date extractExpiration(String token) {
        log.debug("Extracting expiration date from token");
        return extractClaim(token, Claims::getExpiration);
    }
}
