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

import java.io.IOException;
import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            return true;
//        }
//        return request.getServletPath().startsWith("/auth/");
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        String username;
//
//        try {
//            username = jwtService.extractUsername(token); // sub
//        } catch (Exception e) {
//            logger.warn("Invalid JWT token", e);
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//
//        if (username != null &&
//                SecurityContextHolder.getContext().getAuthentication() == null &&
//                jwtService.isTokenValid(token, username)) {
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            username,
//                            null,
//                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
//                    );
//
//
//            authentication.setDetails(
//                    new WebAuthenticationDetailsSource().buildDetails(request)
//            );
//
//            SecurityContextHolder.getContext()
//                    .setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || request.getServletPath().startsWith("/auth/")
                || request.getServletPath().startsWith("/tcba/yard/login");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing access token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            if (jwtService.isTokenExpired(token)) {
                sendUnauthorized(response, "Access token expired");
                return;
            }

            if (!jwtService.isTokenValid(token, username)) {
                sendUnauthorized(response, "Invalid access token");
                return;
            }

            // 🔥 detect who logged in
            String userName = jwtService.extractUsername(token);
            String yardId = jwtService.extractYardId(token);

            if (userName == null && yardId == null) {
                sendUnauthorized(response, "Invalid token claims");
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
            sendUnauthorized(response, "Invalid access token");
        }
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
                {
                  "status": "FAILURE",
                  "message": "%s"
                }
                """.formatted(message));
    }
}


