package com.harshpatil.projects.airBnbProject.security;

import com.harshpatil.projects.airBnbProject.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secreteKey;

    private SecretKey getSecreteKey()
    {
        return Keys.hmacShaKeyFor(secreteKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAssessToken(User user)
    {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email",user.getEmail())
                .claim("roles",user.getRoles().toString())
                .claim("type","access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000L*60*60))
                .signWith(getSecreteKey())
                .compact();
    }

    public String generateRefreshToken(User user)
    {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("type","refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L*60*60*24*30*6 ))
                .signWith(getSecreteKey())
                .compact();
    }

    public Long getUserIdFromToken(String token)
    {
        Claims claims= Jwts.parser()
                .verifyWith(getSecreteKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public String getTokenType(String token)
    {
        Claims claims=Jwts.parser()
                .verifyWith(getSecreteKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("type",String.class);
    }
}
