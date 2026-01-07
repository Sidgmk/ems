package com.ems.ems.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // how long the token is valid (1 hour)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    // secret key used to sign and verify tokens
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username, String role) {

        return Jwts.builder()
                .claim("role", role)                        // Add role to token
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }


    // extract username from token
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // method to extract claims using a resolver function
    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)                          // validate signature
                .build()
                .parseClaimsJws(token)                       // parse full token
                .getBody();                                  // extract payload
        return resolver.apply(claims);
    }

    // validate token
    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    // check expiration
    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
