package com.spoony.spoony_server.adapter.auth.in.web;

import com.spoony.spoony_server.adapter.auth.dto.request.PlatformRequestDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserSignupDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.LoginResponseDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;
import com.spoony.spoony_server.application.auth.port.in.*;
import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.global.auth.annotation.UserId;
import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import com.spoony.spoony_server.global.dto.ResponseDTO;
import io.micrometer.common.lang.Nullable;
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

    private final SignupUseCase signupUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final RefreshUseCase refreshUseCase;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "회원 가입 API, Token Set을 발급합니다.")
    public ResponseEntity<ResponseDTO<UserTokenDTO>> signup(
            @NotBlank @RequestHeader(AuthConstant.AUTHORIZATION_HEADER) final String platformToken,
            @Valid @RequestBody final UserSignupDTO userSignupDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(signupUseCase.signup(platformToken, userSignupDTO)));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "사용자 로그인 API, 성공 시 Token Set, 실패 시 회원가입이 필요합니다.")
    public ResponseEntity<ResponseDTO<LoginResponseDTO>> login(
            @NotBlank @RequestHeader(AuthConstant.AUTHORIZATION_HEADER) final String platformToken,
            @RequestBody final PlatformRequestDTO platformRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(loginUseCase.login(platformRequestDTO.platform(), platformToken)));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "마이페이지 > 설정에서 로그아웃합니다.")
    public ResponseEntity<ResponseDTO<Void>> logout(
            @UserId Long userId
    ) {
        logoutUseCase.logout(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "회원 탈퇴 API", description = "마이페이지 > 설정에서 회원 탈퇴합니다.")
    public ResponseEntity<ResponseDTO<Void>> withdraw(
            @UserId Long userId,
            @Nullable @RequestHeader(value = AuthConstant.APPLE_WITHDRAW_HEADER, required = false) final String authCode
    ) {
        withdrawUseCase.withdraw(userId, authCode);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급 API", description = "Refresh Token을 통한 토큰 재발급 API, Token Set을 발급합니다.")
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
