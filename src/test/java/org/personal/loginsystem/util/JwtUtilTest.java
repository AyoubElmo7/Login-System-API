package org.personal.loginsystem.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class JwtUtilTest {

    private final long expirationTime = 60 * 60 * 1000;

    @Autowired
    private JwtUtil jwtUtil = new JwtUtil();

    @Test
    void getSigningKey_authKey_returnsValidKey() {
        Key authKey = jwtUtil.getSigningKey(true);

        assertNotNull(authKey);
    }

    @Test
    void getSigningKey_passwordKey_returnsValidKey() {
        Key passwordKey = jwtUtil.getSigningKey(false);

        assertNotNull(passwordKey);
    }

    @Test
    void generateToken_authToken_isValid() {
        String token = jwtUtil.generateToken("Testing", expirationTime, true);

        assertTrue(jwtUtil.validateToken(token, true));
        assertFalse(jwtUtil.validateToken(token, false));
    }

    @Test
    void generateToken_passwordToken_isValid() {
        String token = jwtUtil.generateToken("Testing", expirationTime, false);

        assertTrue(jwtUtil.validateToken(token, false));
        assertFalse(jwtUtil.validateToken(token, true));
    }

    @Test
    void validateToken_authAndPasswordToken_isTrue() {
        String authToken = jwtUtil.generateToken("TestingAuthToken", expirationTime, true);
        String passwordToken = jwtUtil.generateToken("TestingPasswordToken", expirationTime, false);

        assertTrue(jwtUtil.validateToken(authToken, true));
        assertTrue(jwtUtil.validateToken(passwordToken, false));
    }

    @Test
    void validateToken_authAndPasswordToken_isFalse() {
        String authToken = jwtUtil.generateToken("TestingAuthToken", expirationTime, true);
        String passwordToken = jwtUtil.generateToken("TestingPasswordToken", expirationTime, false);

        assertFalse(jwtUtil.validateToken(authToken, false));
        assertFalse(jwtUtil.validateToken(passwordToken, true));
    }

    @Test
    void validateToken_tokenExpiredTimePassed_isFalse() {
        String authToken = jwtUtil.generateToken("TestingAuthToken", 0L, true);
        String passwordToken = jwtUtil.generateToken("TestingPasswordToken", 0L, false);

        assertFalse(jwtUtil.validateToken(authToken, false));
        assertFalse(jwtUtil.validateToken(passwordToken, true));
    }

    @Test
    void getUsernameOrEmailFromToken_authAndPasswordToken_getsUsername() {
        String authToken = jwtUtil.generateToken("TestingAuthToken", expirationTime, true);
        String passwordToken = jwtUtil.generateToken("TestingPasswordToken", expirationTime, false);

        String authUsername = jwtUtil.getUsernameOrEmailFromToken(authToken, true);
        String passwordUsername = jwtUtil.getUsernameOrEmailFromToken(passwordToken, false);

        assertEquals("TestingAuthToken", authUsername);
        assertEquals("TestingPasswordToken", passwordUsername);
    }
}
