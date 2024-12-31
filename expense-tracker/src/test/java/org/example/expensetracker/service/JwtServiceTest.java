package org.example.expensetracker.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private Claims claims;

    private String secretKey;
    private final String testUsername = "testUser";
    private String token;

    @BeforeEach
    void setUp() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());

        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);

        token = Jwts.builder()
                .setSubject(testUsername)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

    @Test
    void whenExtractUsername_withValidToken_positiveScenario() {
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(testUsername, extractedUsername, "The extracted username should match the test username");
    }

    @Test
    void whenExtractUsername_withInvalidToken_failureScenario() {
        String invalidToken = "invalid.token.string";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken),
                "Extracting username from an invalid token should throw JwtException");
    }

    @Test
    void whenExtractClaim_withMockedClaims_positiveScenario() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String validToken = Jwts.builder()
                .setSubject("mockedUser")
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Valid for 1 hour
                .signWith(key)
                .compact();

        String claimValue = jwtService.extractClaim(validToken, Claims::getSubject);

        assertEquals("mockedUser", claimValue, "The extracted claim should match the mocked value");
    }


    @Test
    void whenExtractClaim_withInvalidToken_failureScenario() {
        String invalidToken = "invalid.token.string";

        assertThrows(JwtException.class, () -> jwtService.extractClaim(invalidToken, Claims::getSubject),
                "Extracting claims from an invalid token should throw JwtException");
    }

    @Test
    void whenIsTokenExpired_withExpiredToken_positiveScenario() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60))
                .signWith(key)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenExpired(expiredToken));
    }


    @Test
    void whenIsTokenExpired_withInvalidToken_failureScenario() {
        String invalidToken = "invalid.token.string";

        assertThrows(JwtException.class, () -> jwtService.isTokenExpired(invalidToken),
                "Checking expiration on an invalid token should throw JwtException");
    }

    @Test
    void whenIsTokenExpired_withFutureToken_positiveScenario() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String validFutureToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key)
                .compact();

        boolean expired = jwtService.isTokenExpired(validFutureToken);

        assertFalse(expired, "The token should not be marked as expired if the expiration date is in the future");
    }
}
