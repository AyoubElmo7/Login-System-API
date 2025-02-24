package org.personal.loginsystem.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.personal.loginsystem.entities.AuthResponse;
import org.personal.loginsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private long expirationTime = 5 * 60 * 60 * 1000;
    private final JwtUtil jwtUtil;

    @Autowired
    public OAuth2LoginSuccessHandler(final JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String username = jwtUtil.generateToken(extractUsername(authentication), expirationTime, true);

        String token = jwtUtil.generateToken(username, expirationTime, true);

        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(new AuthResponse(token)));
    }

    private String extractUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        return switch (principal) {
            case OAuth2User oAuth2User -> oAuth2User.getAttribute("email");
            case UserDetails userDetails -> userDetails.getUsername();
            case String username -> username;
            default -> authentication.getName();
        };
    }
}
