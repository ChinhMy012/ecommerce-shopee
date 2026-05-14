package com.example.ecommerce_backend.security;

import com.example.ecommerce_backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j // Dùng để log lỗi nếu cần
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate; // Inject Redis để check blacklist

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lấy Token từ Header hoặc Cookie
        String token = extractToken(request);

        // Nếu không có token, bỏ qua filter này chuyển sang filter tiếp theo
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. KIỂM TRA BLACKLIST TRONG REDIS
            // Nếu token tồn tại trong Redis, coi như đã logout hoặc bị thu hồi
            Boolean isBlacklisted = redisTemplate.hasKey(token);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.warn("Token is blacklisted: {}", token);
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has been revoked/logged out");
                return;
            }

            // 3. Trích xuất username từ Token
            String username = jwtService.extractUsername(token);

            // 4. Nếu có username và chưa được xác thực trong Context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Kiểm tra tính hợp lệ của token (chữ ký, hết hạn)
                if (jwtService.isTokenValid(token, username)) {

                    // Lấy danh sách quyền từ Token
                    List<String> permissions = jwtService.extractAuthorities(token);

                    List<SimpleGrantedAuthority> authorities = permissions.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    // Tạo đối tượng Authentication
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

                    // Lưu vào Context của Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            // Bạn có thể tùy chọn trả về 401 ở đây nếu muốn bắt chặt chẽ lỗi hết hạn
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Helper: Lấy token linh hoạt từ Authorization Header hoặc Cookie "access_token"
     */
    private String extractToken(HttpServletRequest request) {
        // Ưu tiên 1: Lấy từ Header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Ưu tiên 2: Lấy từ Cookie (Dành cho trình duyệt)
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "access_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}