package com.example.TCBA.Config;

import com.example.TCBA.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || request.getServletPath().startsWith("/auth/")
                || request.getServletPath().startsWith("/tcba/yard/login")
                || request.getServletPath().startsWith("/api/webhook/razorpay");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing access token","Missing access token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            if (jwtService.isTokenExpired(token)) {
                sendUnauthorized(response, "Access token expired","TOKEN_EXPIRED");
                return;
            }

            if (!jwtService.isTokenValid(token, username)) {
                sendUnauthorized(response, "Invalid access token", "Invalid access token");
                return;
            }

            // 🔥 detect who logged in
            String userName = jwtService.extractUsername(token);
            String yardId = jwtService.extractYardId(token);

            if (userName == null && yardId == null) {
                sendUnauthorized(response, "Invalid token claims", "Invalid token claims");
                return;
            }

            // ✅ authentication success
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendUnauthorized(response, "Invalid access token","TOKEN_EXPIRED");
        }
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String message,
            String errorCode
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
                {
                  "status": "FAILURE",
                  "message": "%s",
                  "errorCode": "%s"
                }
                """.formatted(message,errorCode));
    }
}


