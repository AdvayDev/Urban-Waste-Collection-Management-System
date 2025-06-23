package com.wastewise.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String Secret_key = "256-bit-secret-key"; //to be replaced by environment variable

    private final long Expiration_time = 1000 * 60 * 60 * 10; //10 hours

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(Secret_key.getBytes());
    }

    public String generateToken(String workerId, String role){
        return Jwts.builder()
                .setSubject(workerId)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + Expiration_time))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        }
        catch (JwtException e){
            return false;
        }
    }

    public String extractWorkerId(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String extractRole(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("role",String.class);
    }
}