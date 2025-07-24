package com.spoony.spoony_server.global.auth.jwt;

import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import com.spoony.spoony_server.global.auth.dto.ClaimDTO;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class AdminJwtTokenProvider implements InitializingBean {

    private static final String ANONYMOUS_USER = "anonymous";

    @Value("${jwt.admin.access_token_expiration_time}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.admin.secret}")
    private String secretKey;

    private Key signingKey;

    @Override
    public void afterPropertiesSet() {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.signingKey = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    public String generateToken(Long adminId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationTime);

        Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(expiry);

        claims.put(AuthConstant.USER_ID, adminId);
        claims.put(AuthConstant.TOKEN_TYPE, true);
        claims.put("role", "ADMIN");

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(signingKey)
                .compact();
    }

    public Claims getClaims(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorMessage.INVALID_TOKEN);
        }
    }

    public ClaimDTO getClaimFromToken(String token) {
        Claims claims = getClaims(token);
        return ClaimDTO.of(
                Long.valueOf(claims.get(AuthConstant.USER_ID).toString()),
                Boolean.parseBoolean(claims.get(AuthConstant.TOKEN_TYPE).toString())
        );
    }

    public static Object validatePrincipal(final Object principal) {
        if (ANONYMOUS_USER.equals(principal)) {
            throw new AuthException(AuthErrorMessage.UNAUTHORIZED);
        }
        return principal;
    }
}
