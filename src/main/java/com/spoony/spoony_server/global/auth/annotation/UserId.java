package com.spoony.spoony_server.global.auth.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression="T(com.spoony.spoony_server.global.auth.jwt.JwtTokenProvider).validatePrincipal(#this)")
public @interface UserId {
}
