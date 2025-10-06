package com.sih.AuthService.service;

import com.sih.AuthService.config.AppCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTGenerator {

    @Autowired
    private AppCache appCache;

    @Value("${access_token_expiration}")
    private long accessTokenExpirationMillis;

    @Value("${refresh_token_expiration}")
    private long refreshTokenExpirationMillis; // 7 days

    public String generateAccessToken(String username) {
        return generateTokenWithExpiration(username, accessTokenExpirationMillis);
    }

    public String generateRefreshToken(String username) {
        return generateTokenWithExpiration(username, refreshTokenExpirationMillis); // 7 days
    }

    public String generateTokenWithExpiration(String username, long expirationMillis) {
        // the contents of the payload in a JWT (JSON Web Token) are called claims.
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims) // add claims (currently empty)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .and()
                .signWith(getKey()) // sign the token with the secret key
                .compact();

    }

    private SecretKey getKey() {
        // just assigning key for development purposes
        String secretKey = "c2VjcmV0S2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=";//appCache.appCacheValues.get(AppCache.CacheKeys.SECRET_KEY.toString()); // property injection from DB
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}