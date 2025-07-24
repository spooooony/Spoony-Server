package com.spoony.spoony_server.global.auth.filter;

import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import com.spoony.spoony_server.global.auth.jwt.AdminJwtTokenProvider;
import com.spoony.spoony_server.global.auth.jwt.AdminJwtTokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminJwtAuthenticationFilter extends OncePerRequestFilter {

    private final AdminJwtTokenProvider adminJwtTokenProvider;
    private final AdminJwtTokenValidator adminJwtTokenValidator;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null) {
            adminJwtTokenValidator.validate(token); // 유효성 검증
            Long adminId = adminJwtTokenProvider.getClaimFromToken(token).userId();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(adminId, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
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
        return !requestURI.startsWith("/api/v1/admin");  // /admin 요청에만 필터 적용
    }
}
