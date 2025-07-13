package com.example.stageproj.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.example.stageproj.entity.User;

@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKey}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            System.out.println("JwtService - Extracted username from token: " + username);
            return username;
        } catch (Exception e) {
            System.out.println("JwtService - Error extracting username: " + e.getMessage());
            throw e;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("JwtService - Successfully parsed JWT claims");
            return claims;
        } catch (Exception e) {
            System.out.println("JwtService - Error parsing JWT claims: " + e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean expired = expiration.before(new Date());
        System.out.println("JwtService - Token expiration check: " + expiration + ", expired: " + expired);
        return expired;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole()); // Add this line
        String token = createToken(claims, user.getUsername());
        System.out.println("JwtService - Generated token for user: " + user.getUsername());
        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean usernameMatch = username.equals(userDetails.getUsername());
            boolean notExpired = !isTokenExpired(token);
            boolean valid = usernameMatch && notExpired;
            
            System.out.println("JwtService - Token validation:");
            System.out.println("  - Username match: " + usernameMatch + " (token: " + username + ", userDetails: " + userDetails.getUsername() + ")");
            System.out.println("  - Not expired: " + notExpired);
            System.out.println("  - Overall valid: " + valid);
            
            return valid;
        } catch (Exception e) {
            System.out.println("JwtService - Token validation failed with exception: " + e.getMessage());
            return false;
        }
    }
} 