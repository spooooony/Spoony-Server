package com.spoony.spoony_server.global.config;

import com.spoony.spoony_server.global.auth.filter.JwtAuthenticationFilter;
import com.spoony.spoony_server.global.auth.filter.JwtExceptionFilter;
import com.spoony.spoony_server.global.auth.handler.CustomJwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomJwtAuthenticationEntryPoint authenticationEntryPoint;

    private static final List<String> AUTH_WHITE_LIST = List.of(
            "/api/v1/auth/signin",
            "/api/v1/auth/refresh",
            "/api/v1/user/exists",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(AUTH_WHITE_LIST.toArray(new String[0])).permitAll()
                .anyRequest().authenticated());

        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}
