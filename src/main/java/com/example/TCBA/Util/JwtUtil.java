package com.example.TCBA.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String getLoggedInEmail(HttpServletRequest request) {
        // Already implemented JWT filter irundha
        // SecurityContextHolder la irundhu edukanum
        return request.getUserPrincipal().getName();
    }
}
