package com.example.TCBA.Service;

import com.example.TCBA.Entity.BrokerLogin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class JwtService {

@Value("${jwt.secret}")
private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private byte[] getKey() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(Keys.hmacShaKeyFor(getKey()))
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(Keys.hmacShaKeyFor(getKey()))
                .compact();
    }

    public String generateYardToken(BrokerLogin yard) {

        return Jwts.builder()
                .setSubject(yard.getEmail())
                .claim("yardId", yard.getStackHolderId())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 30 * 60 * 1000)
                )
                .signWith(Keys.hmacShaKeyFor(getKey()))
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractYardId(String token) {
        return extractAllClaims(token).get("yardId", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // ✅ ADD THIS
    public boolean isTokenValid(String token, String username) {
        try {
            return extractUsername(token).equals(username)
                    && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(getKey()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
