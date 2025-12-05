package com.petadoption.backend.config;

import com.petadoption.backend.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("JWT Filter - Request URI: " + request.getRequestURI());
        System.out.println("JWT Filter - Authorization header: " + (authorizationHeader != null ? "present" : "null"));

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JWT Filter - JWT token present, length: " + jwt.length());
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("JWT Filter - Extracted username: " + username);
            } catch (Exception e) {
                // Invalid token, continue without authentication
                System.out.println("JWT Filter - Error extracting username: " + e.getMessage());
                username = null;
            }
        } else {
            System.out.println("JWT Filter - No Bearer token found");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("JWT Filter - Loaded user: " + userDetails.getUsername());
                System.out.println("JWT Filter - User authorities: " + userDetails.getAuthorities());

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("JWT Filter - Authentication successful for user: " + userDetails.getUsername());
                } else {
                    System.out.println("JWT Filter - Token validation failed for user: " + username);
                }
            } catch (Exception e) {
                // Invalid user or token, continue without authentication
                System.out.println("JWT Filter - Error loading user: " + username + ", error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
