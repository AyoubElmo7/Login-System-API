package org.personal.loginsystem.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.personal.loginsystem.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OAuth2LoginSuccessHandler OAuthSuccessHandler;

    private final long expirationTime = 5 * 60 * 60 * 1000;
    private final String expectedToken = "mocked-jwt-token";
    private final String email = "test@example.com";
    private final String username = "testUsername";

    @Test
    void onAuthenticationSuccess_usernameString_generateTokenAndWriteResponse() throws IOException {
        when(jwtUtil.generateToken(email, expirationTime, true)).thenReturn(expectedToken);
        when(authentication.getPrincipal()).thenReturn(email);
        PrintWriter writer = new PrintWriter(new StringWriter());
        when(response.getWriter()).thenReturn(writer);

        OAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtUtil, times(1)).generateToken(email, expirationTime, true);
        verify(response, times(1)).setContentType("application/json");
    }

    @Test
    void onAuthenticationSuccess_userDetailsHoldsUsername_generateTokenAndWriteResponse() throws IOException {
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername()).thenReturn(username);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(username, expirationTime, true)).thenReturn(expectedToken);

        PrintWriter writer = new PrintWriter(new StringWriter());
        when(response.getWriter()).thenReturn(writer);

        OAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtUtil, times(1)).generateToken(username, expirationTime, true);
        verify(response, times(1)).setContentType("application/json");
    }

    @Test
    void onAuthenticationSuccess_OAuth2UserDetailsHoldsUsername_generateTokenAndWriteResponse() throws IOException {
        OAuth2User oAuth2User = mock(OAuth2User.class);

        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(jwtUtil.generateToken(email, expirationTime, true)).thenReturn(expectedToken);

        PrintWriter writer = new PrintWriter(new StringWriter());
        when(response.getWriter()).thenReturn(writer);

        OAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtUtil, times(1)).generateToken(email, expirationTime, true);
        verify(response, times(1)).setContentType("application/json");
    }

    @Test
    void onAuthenticationSuccess_authenticationHoldsName_generateTokenAndWriteResponse() throws IOException {
        when(authentication.getName()).thenReturn(username);
        when(jwtUtil.generateToken(username, expirationTime, true)).thenReturn(expectedToken);
        when(authentication.getPrincipal()).thenReturn(new Object());

        PrintWriter writer = new PrintWriter(new StringWriter());
        when(response.getWriter()).thenReturn(writer);

        OAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(jwtUtil, times(1)).generateToken(username, expirationTime, true);
        verify(response, times(1)).setContentType("application/json");
    }

    @Test
    void onAuthenticationSuccess_responseGetWriterFails_throwsIOException() throws IOException {
        when(authentication.getName()).thenReturn(username);
        when(jwtUtil.generateToken(username, expirationTime, true)).thenReturn(expectedToken);
        when(authentication.getPrincipal()).thenReturn(new Object());

        when(response.getWriter()).thenThrow(new IOException());

        assertThatThrownBy(() -> OAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(IOException.class);
    }
}
