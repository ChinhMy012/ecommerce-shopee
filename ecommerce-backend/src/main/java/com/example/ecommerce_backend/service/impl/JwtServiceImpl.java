package com.example.ecommerce_backend.service.impl;

import com.example.ecommerce_backend.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final Key signingKey;
    private final long jwtExpirationMs;          // Access token
    private final long refreshTokenExpirationMs; // Refresh token

    public JwtServiceImpl(
            @Value("${ecommerce.security.jwt-secret}") String secret,
            @Value("${ecommerce.security.jwt-expiration-ms}") long jwtExpirationMs,
            @Value("${ecommerce.security.jwt-refresh-expiration-ms}") long refreshTokenExpirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    // ========================
    // ACCESS TOKEN
    // ========================
    @Override
    public String generateToken(String subject, Set<String> authorities) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ========================
    // REFRESH TOKEN
    // ========================
    @Override
    public String generateRefreshToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ========================
    // EXTRACT DATA
    // ========================
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<String> extractAuthorities(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("authorities", List.class);
    }

    // ========================
    // VALIDATION
    // ========================
    @Override
    public boolean isTokenValid(String token, String username) {
        try {
            String tokenUsername = extractUsername(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ========================
    // PRIVATE HELPERS
    // ========================
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }
}
