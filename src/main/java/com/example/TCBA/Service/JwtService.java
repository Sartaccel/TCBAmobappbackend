package com.example.TCBA.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

//    @Value("${jwt.secret:tcba_default_secret_key_1234567890123456}")
//    private String secret;
//
//    @Value("${jwt.expiration}")
//    private long expiration;
//
//    public String generateToken(String username) {
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(
//                        Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))
//                )
//                .compact();
//    }
//
//    public String extractUsername(String token) {
//        return extractAllClaims(token).getSubject();
//    }
//
//    // 🔹 NEW
//    public boolean isTokenValid(String token, String username) {
//        try {
//            String tokenUsername = extractUsername(token);
//            return tokenUsername.equals(username) && !isTokenExpired(token);
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    // 🔹 NEW
//    private boolean isTokenExpired(String token) {
//        Date expiration = extractAllClaims(token).getExpiration();
//        return expiration.before(new Date());
//    }
//
//    // 🔹 NEW
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(
//                        Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))
//                )
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }

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

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
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
