package com.moviebookingapp.backend.security;

public class TokenHelper {

    public static String getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        String token = authHeader.substring(7);
        try {
            return JwtUtil.decodeToken(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRoleFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        String token = authHeader.substring(7);
        try {
            return JwtUtil.decodeToken(token).get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
