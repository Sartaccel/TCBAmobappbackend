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

    @Value("${jwt.yard-token-expiration}")
    private long yardTokenExpiration;


    private byte[] getKey() {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateAccessToken(
            String email,
            String stackHolderId,
            String legalName
    ) {

        return Jwts.builder()
                .setSubject(email)
                .claim("stackHolderId", stackHolderId)
                .claim("legalName", legalName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(Keys.hmacShaKeyFor(getKey()))
                .compact();
    }


    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
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
                        new Date(System.currentTimeMillis() + yardTokenExpiration)
                )
                .signWith(Keys.hmacShaKeyFor(getKey()))
                .compact();
    }

    public String generateClientAccessToken(String clientId) {

        return Jwts.builder()
                .setSubject(clientId)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + accessExpiration)
                )
                .signWith(Keys.hmacShaKeyFor(getKey()))
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractYardId(String token) {
        return extractAllClaims(token).get("yardId", String.class);
    }

    public String extractLegalName(String token) {
        return extractAllClaims(token).get("legalName", String.class);
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

    public String extractStackHolderId(String token) {
        return extractAllClaims(token).get("stackHolderId", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(getKey()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
