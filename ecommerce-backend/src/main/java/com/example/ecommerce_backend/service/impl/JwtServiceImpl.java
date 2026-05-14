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
    private final long jwtExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtServiceImpl(
            @Value("${ecommerce.security.jwt-secret}") String secret,
            @Value("${ecommerce.security.jwt-expiration-ms}") long jwtExpirationMs,
            @Value("${ecommerce.security.jwt-refresh-expiration-ms}") long refreshTokenExpirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public String generateToken(String subject, Set<String> authorities) {
        return buildToken(subject, jwtExpirationMs, authorities);
    }

    @Override
    public String generateRefreshToken(String subject) {
        return buildToken(subject, refreshTokenExpirationMs, null);
    }

    private String buildToken(String subject, long expiration, Set<String> authorities) {
        Date now = new Date();
        var builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration));

        if (authorities != null) {
            builder.claim("authorities", authorities);
        }

        return builder.signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<String> extractAuthorities(String token) {
        return extractClaim(token, claims -> claims.get("authorities", List.class));
    }

    // HÀM MỚI QUAN TRỌNG CHO REDIS
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public boolean isTokenValid(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}