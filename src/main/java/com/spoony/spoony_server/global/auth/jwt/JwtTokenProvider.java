package com.spoony.spoony_server.global.auth.jwt;

import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import com.spoony.spoony_server.global.auth.dto.ClaimDTO;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider implements InitializingBean {

    private static final String ANONYMOUS_USER = "anonymous";

    @Value("${jwt.access_token_expiration_time}")
    private Long accessTokenExpirationTime;
    @Value("${jwt.refresh_token_expiration_time}")
    private Long refreshTokenExpirationTime;
    @Value("${jwt.secret}")
    private String secretKey;

    private Key singingKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.singingKey = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    public JwtTokenDTO generateTokenPair(Long userId) {
        return JwtTokenDTO.of(
                generateToken(userId, true),
                generateToken(userId, false)
        );
    }

    public String generateToken(Long userId, boolean isAccessToken) {
        final Date now = new Date();
        final Date expirationDate = generateExpirationDate(now, isAccessToken);
        final Claims claims = Jwts.claims()
                .setIssuedAt(now)
                .setExpiration(expirationDate);

        claims.put(AuthConstant.USER_ID, userId);
        claims.put(AuthConstant.TOKEN_TYPE, isAccessToken);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(singingKey)
                .compact();
    }

    private Date generateExpirationDate(Date now, boolean isAccessToken) {
        if (isAccessToken) {
            return new Date(now.getTime() + accessTokenExpirationTime);
        }
        return new Date(now.getTime() + refreshTokenExpirationTime);
    }

    public Claims getClaims(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(singingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
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
