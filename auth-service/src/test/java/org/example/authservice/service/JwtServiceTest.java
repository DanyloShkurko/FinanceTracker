package org.example.authservice.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.authservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        int expiration = 3600000;
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expiration);

        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
    }

    @Test
    void whenGenerateToken_givenUser_thenTokenIsGenerated() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void whenGenerateTokenWithExtraClaims_givenUser_thenTokenIsGenerated() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "admin");

        String token = jwtService.generateToken(extraClaims, user);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void whenGetSecretKey_thenSecretKeyDecoded() {
        String secretKey = jwtService.getSecretKey();

        assertNotNull(secretKey);
        assertFalse(secretKey.isEmpty());
    }

    @Test
    void whenGetJwtExpiration_thenExpirationTimeIsCorrect() {
        long expirationTime = jwtService.getJwtExpiration();

        assertEquals(3600000, expirationTime);
    }
}
