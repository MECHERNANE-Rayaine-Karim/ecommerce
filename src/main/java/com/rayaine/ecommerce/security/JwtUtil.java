package com.rayaine.ecommerce.security;

import com.rayaine.ecommerce.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.parser;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken( String username , User.Role role ){
        return builder().subject(username).claim("role",role.name()).issuedAt(new Date()).
                expiration(new Date(System.currentTimeMillis() + expiration)).
                signWith(getSigningKey()).compact();
    }

    public String extractUsername( String token){
        return parser().verifyWith(getSigningKey()).build().
                parseSignedClaims(token).getPayload().getSubject();
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean isTokenValid( String username , String token ){
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token ){
        return parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getExpiration()
                .before(new Date());
    }

}

