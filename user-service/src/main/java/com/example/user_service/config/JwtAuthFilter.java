package com.example.user_service.config;

import com.example.user_service.entity.User;
import com.example.user_service.service.JwtService;
import com.example.user_service.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public JwtAuthFilter(HandlerExceptionResolver handlerExceptionResolver,
                         JwtService jwtService,
                         UserService userService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization header is missing or does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            log.info("Extracted JWT token: {}", jwt);

            final String email = jwtService.extractUsername(jwt);
            log.debug("Extracted username from JWT: {}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = this.userService.getUserByEmail(email);
                user.setUsername(email);
                log.info("User found: {}", email);

                if (jwtService.isTokenValid(jwt, user)) {
                    log.info("JWT token is valid for user: {}", email);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            ((UserDetails) user).getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT token is invalid for user: {}", email);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("Exception occurred during JWT authentication", exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
