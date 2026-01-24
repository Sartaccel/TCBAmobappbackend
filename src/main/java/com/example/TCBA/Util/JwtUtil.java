package com.example.TCBA.Util;

import com.example.TCBA.Entity.BrokerLogin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET =
            "TCBA_SUPER_SECRET_KEY_2026";

    private final long EXPIRY =
            1000 * 60 * 1;

    public String generateToken(BrokerLogin user) {

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("yardId", user.getStackHolderId())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRY)
                )
                .signWith(
                        Keys.hmacShaKeyFor(SECRET.getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(
                        Keys.hmacShaKeyFor(SECRET.getBytes())
                )
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getLoggedInEmail(HttpServletRequest request) {
        // Already implemented JWT filter irundha
        // SecurityContextHolder la irundhu edukanum
        return request.getUserPrincipal().getName();
    }

}
