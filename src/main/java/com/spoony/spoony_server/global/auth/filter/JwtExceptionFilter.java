package com.spoony.spoony_server.global.auth.filter;

import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import com.spoony.spoony_server.global.message.business.BusinessErrorMessage;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (AuthException e) {
            request.setAttribute("exception", e.getErrorMessage());
            chain.doFilter(request, response);
        } catch (JwtException e) {
            request.setAttribute("exception", AuthErrorMessage.UNKNOWN_TOKEN);
            chain.doFilter(request, response);
        } catch (Exception e) {
            request.setAttribute("exception", BusinessErrorMessage.INTERNAL_SERVER_ERROR);
            chain.doFilter(request, response);
        }
    }
}
