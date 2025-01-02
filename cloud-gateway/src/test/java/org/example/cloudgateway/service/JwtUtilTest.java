package org.example.cloudgateway.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String secretKey;
    private String token;
    private final String testUsername = "testUser";

    @BeforeEach
    void setUp() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

        ReflectionTestUtils.setField(jwtUtil, "secret", secretKey);

        token = Jwts.builder()
                .setSubject(testUsername)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    @Test
    void whenExtractAllClaims_withValidToken_positiveScenario() {
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals(testUsername, claims.getSubject());
    }

    @Test
    void whenExtractAllClaims_withInvalidToken_failureScenario() {
        String invalidToken = "invalid.token.string";

        assertThrows(JwtException.class, () -> jwtUtil.extractAllClaims(invalidToken));
    }

    @Test
    void whenIsTokenExpired_withExpiredToken_positiveScenario() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String expiredToken = Jwts.builder()
                .setSubject(testUsername)
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(key)
                .compact();

        assertTrue(jwtUtil.isTokenExpired(expiredToken));
    }

    @Test
    void whenIsTokenExpired_withValidToken_negativeScenario() {
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void whenIsInvalid_withValidToken_negativeScenario() {
        assertFalse(jwtUtil.isInvalid("Bearer " + token));
    }

    @Test
    void whenIsInvalid_withExpiredToken_positiveScenario() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String expiredToken = Jwts.builder()
                .setSubject(testUsername)
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60)) // Истекший токен
                .signWith(key)
                .compact();

        assertTrue(jwtUtil.isInvalid(expiredToken));
    }

    @Test
    void whenIsInvalid_withMissingAuthHeader_positiveScenario() {
        String invalidToken = "invalidTokenString";

        assertTrue(jwtUtil.isInvalid(invalidToken));
    }

    @Test
    void whenIsAuthMissing_withValidToken_negativeScenario() {
        assertFalse(jwtUtil.isAuthMissing("Bearer " + token));
    }

    @Test
    void whenIsAuthMissing_withInvalidToken_positiveScenario() {
        String invalidToken = "invalidTokenString";

        assertTrue(jwtUtil.isAuthMissing(invalidToken));
    }
}
