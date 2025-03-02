package org.personal.loginsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.personal.loginsystem.util.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_requestContainsValidHeader() throws Exception {
        String token = "Bearer validToken" ;
        String username = "TestUser";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.getUsernameOrEmailFromToken("validToken", true)).thenReturn(username);
        when(jwtUtil.validateToken("validToken", true)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(mock(UserDetails.class));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_InvalidToken() throws Exception {
        String token = "Bearer invalidToken" ;

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.getUsernameOrEmailFromToken("invalidToken", true)).thenReturn("invalidUser");
        when(jwtUtil.validateToken("invalidToken", true)).thenReturn(false);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_NoToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_tokenMissingBearer() throws Exception {
        String token = "MalformedToken";

        when(request.getHeader("Authorization")).thenReturn(token);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_throwsIOException() throws Exception {
        doThrow(new IOException()).when(filterChain).doFilter(request, response);

        when(request.getHeader("Authorization")).thenReturn(null);

        assertThatThrownBy(() -> jwtRequestFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(IOException.class);
    }

    @Test
    void doFilterInternal_throwsServletException() throws Exception {
        doThrow(new ServletException()).when(filterChain).doFilter(request, response);

        when(request.getHeader("Authorization")).thenReturn(null);

        assertThatThrownBy(() -> jwtRequestFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(ServletException.class);
    }
}
