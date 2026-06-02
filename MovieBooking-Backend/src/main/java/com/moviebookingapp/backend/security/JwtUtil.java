package com.moviebookingapp.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "thisIsA256BitLongSecretKeyForHS256AlgorithmXYZ123";

    private static final long EXPIRATION = 1000 * 60 * 60 * 5; // 5 hours

    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims decodeToken(String token) {
    return Jwts.parser()
            .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
            .parseClaimsJws(token)
            .getBody();
}

}
