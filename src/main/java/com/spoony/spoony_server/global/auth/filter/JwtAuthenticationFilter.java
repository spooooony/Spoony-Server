package com.spoony.spoony_server.global.auth.filter;

import com.spoony.spoony_server.global.auth.jwt.AdminJwtTokenProvider;
import com.spoony.spoony_server.global.auth.jwt.AdminJwtTokenValidator;
import com.spoony.spoony_server.global.auth.jwt.JwtTokenProvider;
import com.spoony.spoony_server.global.auth.jwt.JwtTokenValidator;
import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AdminJwtTokenProvider adminJwtTokenProvider;
    private final AdminJwtTokenValidator adminJwtTokenValidator;
    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String token = resolveToken(request);

        if (token != null) {
            if (isAdminRequest(requestURI)) {
                // 관리자 토큰 검증
                adminJwtTokenValidator.validate(token);
                Long adminId = adminJwtTokenProvider.getClaimFromToken(token).userId();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(adminId, null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 일반 사용자 토큰 검증
                jwtTokenValidator.validateAccessToken(token);
                Long userId = jwtTokenProvider.getClaimFromToken(token).userId();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, null); // 일반 유저는 권한 없음
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isAdminRequest(String uri) {
        return uri.startsWith("/api/v1/admin");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConstant.AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(AuthConstant.BEARER_TOKEN_PREFIX)) {
            return bearerToken.substring(AuthConstant.BEARER_TOKEN_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/api/v1/auth/signup")
                || requestURI.startsWith("/api/v1/auth/login")
                || requestURI.startsWith("/api/v1/auth/refresh")
                || requestURI.startsWith("/api/v1/user/exists")
                || requestURI.startsWith("/api/v1/user/region")
                || requestURI.startsWith("/api/v1/admin/login")
                || requestURI.startsWith("/profile-images")
                || requestURI.startsWith("/swagger-ui")
                || requestURI.startsWith("/actuator")
                || requestURI.startsWith("/v3/api-docs");
    }
}
