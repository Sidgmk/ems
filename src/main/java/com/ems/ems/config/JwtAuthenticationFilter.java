package com.ems.ems.config;

import com.ems.ems.service.JwtService;
import com.ems.ems.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;   // loads user from DB

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // If missing or not Bearer -> skip and continue to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token from header
        String token = authHeader.substring(7);

        // Extract username (subject)
        String username = jwtService.extractUsername(token);

        // Proceed only if username not null & user not yet authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load DB info
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate token
            if (jwtService.isTokenValid(token, username)) {

                // Extract role from token
                String role = jwtService.extractClaims(token, claims -> claims.get("role")).toString();

                // Convert role to Spring format
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                // Build authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(authority)
                        );

                // Set authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}
