//package com.example.TCBA.Security;
//
//import com.example.TCBA.Service.JwtService;
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class ClientAuthFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        // allow login + token APIs
//        if (
//                path.contains("/oauth/token")
//                        || path.contains("/yard/login")
//        ) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String authHeader =
//                request.getHeader("Authorization");
//
//        if (authHeader == null ||
//                !authHeader.startsWith("Bearer ")) {
//
//            response.setStatus(401);
//            response.getWriter().write("Missing access token");
//            return;
//        }
//
//        String token = authHeader.substring(7);
//
//        try {
//            String clientId =
//                    jwtService.extractUsername(token);
//
//            if (jwtService.isTokenExpired(token)) {
//                throw new RuntimeException("Expired token");
//            }
//
//            // ✅ AUTH SUCCESS
//            filterChain.doFilter(request, response);
//
//        } catch (Exception e) {
//            response.setStatus(401);
//            response.getWriter().write("Invalid or expired token");
//        }
//    }
//}
