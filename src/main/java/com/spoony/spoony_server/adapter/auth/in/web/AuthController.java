package com.spoony.spoony_server.adapter.auth.in.web;

import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;
import com.spoony.spoony_server.application.auth.port.in.RefreshUseCase;
import com.spoony.spoony_server.application.auth.port.in.SignInUseCase;
import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.spoony.spoony_server.global.auth.constant.AuthConstant.BEARER_TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SignInUseCase signInUseCase;
    private final RefreshUseCase refreshUseCase;

    @PostMapping("/signin")
    @Operation(summary = "회원가입 API", description = "소셜 로그인 회원가입 API, Token Set 발급")
    public ResponseEntity<ResponseDTO<UserTokenDTO>> signIn(
            @NotBlank @RequestHeader(AuthConstant.AUTHORIZATION_HEADER) final String platformToken,
            @Valid @RequestBody final UserLoginDTO userLoginDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(signInUseCase.signIn(platformToken, userLoginDTO)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급 API", description = "Refresh Token을 통한 토큰 재발급 API, Token Set 발급")
    public ResponseEntity<ResponseDTO<JwtTokenDTO>> refreshAccessToken(
            @NotBlank @RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String refreshToken
    ) {
        System.out.println("초기 요청 토큰 " + refreshToken);
        if (refreshToken.startsWith(BEARER_TOKEN_PREFIX)) {
            refreshToken = refreshToken.substring(BEARER_TOKEN_PREFIX.length());
        }
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(refreshUseCase.refreshAccessToken(refreshToken)));
    }
}
