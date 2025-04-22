package com.goldeneggs.Config.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTGenerator {

    private final Key key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * Generates a new JWT token using the authenticated user's username.
     *
     * @param authentication Authentication object from Spring Security.
     * @return JWT as a string.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        System.out.println("New token:");
        System.out.println(token);

        return token;
    }

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token JWT token.
     * @return Username stored in the token's subject.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validates the given JWT token.
     *
     * @param token JWT token.
     * @return true if the token is valid.
     * @throws AuthenticationCredentialsNotFoundException if the token is invalid or expired.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Token expired", e);
        } catch (MalformedJwtException e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token format", e);
        } catch (SignatureException e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token signature", e);
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Invalid token", e);
        }
    }
}
