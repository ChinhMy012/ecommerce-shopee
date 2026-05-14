package com.example.ecommerce_backend.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {

    public void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        addCookie(response, "access_token", accessToken, 15 * 60);
        addCookie(response, "refresh_token", refreshToken, 7 * 24 * 60 * 60);
    }

    public void deleteAuthCookies(HttpServletResponse response) {
        addCookie(response, "access_token", "", 0);
        addCookie(response, "refresh_token", "", 0);
    }

    public void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // On production, set to true
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}