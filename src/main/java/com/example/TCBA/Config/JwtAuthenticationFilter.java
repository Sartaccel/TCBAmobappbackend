//package com.example.TCBA.Config;
//
//
//import com.example.TCBA.Service.JwtService;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//
//        String path = request.getRequestURI();
//
//        return "OPTIONS".equalsIgnoreCase(request.getMethod())
//                || path.contains("/auth/")
//                || path.contains("/tcba/oauth/token")
//                || path.contains("/api/webhook/razorpay");
//    }
//
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws IOException, ServletException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            sendUnauthorized(response, "Missing access token","MISSING_TOKEN");
//            return;
//        }
//
//        String token = authHeader.substring(7);
//
//        try {
//
//            // 🔥 NEW — extract subject
//            String subject = jwtService.extractUsername(token);
//
//            if (jwtService.isTokenExpired(token)) {
//                sendUnauthorized(response, "Access token expired","TOKEN_EXPIRED");
//                return;
//            }
//
//            // 🔥 NEW — check yardId existence
//            String yardId = null;
//            try {
//                yardId = jwtService.extractYardId(token);
//            } catch (Exception ignored) {}
//
//            // =====================================================
//            // ✅ CASE 1 — CLIENT TOKEN
//            // =====================================================
//            if (yardId == null) {
//
//                // client token → only subject exists
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                subject,
//                                null,
//                                List.of()
//                        );
//
//                authentication.setDetails(
//                        new WebAuthenticationDetailsSource()
//                                .buildDetails(request)
//                );
//
//                SecurityContextHolder.getContext()
//                        .setAuthentication(authentication);
//
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // =====================================================
//            // ✅ CASE 2 — YARD TOKEN (EXISTING LOGIC)
//            // =====================================================
//
//            if (!jwtService.isTokenValid(token, subject)) {
//                sendUnauthorized(response, "Invalid access token", "INVALID_TOKEN");
//                return;
//            }
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            subject,
//                            null,
//                            List.of()
//                    );
//
//            authentication.setDetails(
//                    new WebAuthenticationDetailsSource()
//                            .buildDetails(request)
//            );
//
//            SecurityContextHolder.getContext()
//                    .setAuthentication(authentication);
//
//            filterChain.doFilter(request, response);
//
//        } catch (ExpiredJwtException e) {
//            sendUnauthorized(response, "Token expired", "TOKEN_EXPIRED");
//        } catch (Exception e) {
//            sendUnauthorized(response, "Invalid access token","INVALID_TOKEN");
//        }
//    }
//
//    private void sendUnauthorized(
//            HttpServletResponse response,
//            String message,
//            String errorCode
//    ) throws IOException {
//
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//
//        response.getWriter().write("""
//                {
//                  "status": "FAILURE",
//                  "message": "%s",
//                  "errorCode": "%s"
//                }
//                """.formatted(message, errorCode));
//    }
//}

package com.example.TCBA.Config;

import com.example.TCBA.Service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        || request.getServletPath().startsWith("/tcba/oauth/token");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing access token", "TOKEN_MISSING");
            return;
        }

        String token = authHeader.substring(7);

        try {

            String email = jwtService.extractEmail(token);
            String stackHolderId = jwtService.extractStackHolderId(token);
            String legalName = jwtService.extractLegalName(token);

            if (!jwtService.isTokenValid(token, email)) {
                sendUnauthorized(response, "Invalid access token", "INVALID_TOKEN");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                Map<String, Object> claims = new HashMap<>();
                claims.put("email", email);
                claims.put("stackHolderId", stackHolderId);
                claims.put("legalName", legalName);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        }
        catch (ExpiredJwtException e) {
            sendUnauthorized(response, "Access token expired", "TOKEN_EXPIRED");
        }
        catch (Exception e) {
            sendUnauthorized(response, "Invalid access token", "INVALID_TOKEN");
        }
    }

    // Helper method
    private void sendUnauthorized(HttpServletResponse response, String message , String errorCode)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "status": "FAILURE",
              "message": "%s",
              "httpStatus": "UNAUTHORIZED",
              "errorCode" : "%s"
            }
        """.formatted(message,errorCode));
    }
}


