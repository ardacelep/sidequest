package com.tablefour.sidequest.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.core.results.ErrorDataResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final String[] PUBLIC_PATHS = {
            "/api/auth/login",
            "/api/auth/register",
            "/swagger-ui",
            "/v3/api-docs"
    };

    public JwtAuthFilter(JwtService jwtService, UserService userService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(PUBLIC_PATHS).anyMatch(path::startsWith) ||
                request.getMethod().equals("OPTIONS");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            // Skip authentication for OPTIONS requests
            if (request.getMethod().equals("OPTIONS")) {
                filterChain.doFilter(request, response);
                return;
            }

            // If no auth header, continue the chain (let other filters handle it)
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUser(token);

            // If we have a username and no authentication yet
            if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userService.loadUserByUsername(username);

                // Validate token
                if (jwtService.validateToken(token, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Continue the filter chain
            filterChain.doFilter(request, response);

            // Check if response is already committed or has error status
            if (!response.isCommitted() && response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
                // Only handle 401 if it wasn't set by the controller
                handleAuthenticationFailure(response, "Invalid or expired token");
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            handleAuthenticationFailure(response, "Authentication failed: " + e.getMessage());
        }
    }

    private void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorDataResult<String> errorResponse = new ErrorDataResult<>(message, HttpStatus.UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
