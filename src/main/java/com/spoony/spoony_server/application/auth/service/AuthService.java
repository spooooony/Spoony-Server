package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.PlatformRequestDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserSignupDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.LoginResponseDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;
import com.spoony.spoony_server.application.auth.port.in.*;
import com.spoony.spoony_server.application.auth.port.out.AppleRefreshTokenPort;
import com.spoony.spoony_server.application.auth.port.out.TokenPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.auth.jwt.JwtTokenProvider;
import com.spoony.spoony_server.global.auth.jwt.JwtTokenValidator;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements
        SignupUseCase,
        LoginUseCase,
        LogoutUseCase,
        WithdrawUseCase,
        RefreshUseCase {

    private final UserPort userPort;
    private final TokenPort tokenPort;
    private final AppleRefreshTokenPort appleRefreshTokenPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final AppleService appleService;
    private final KakaoService kakaoService;

    @Override
    public UserTokenDTO signup(String platformToken, UserSignupDTO userSignupDTO) {
        PlatformUserDTO platformUserDTO = getPlatformInfo(platformToken, userSignupDTO);
        User user = userPort.create(platformUserDTO, userSignupDTO);

        if (userSignupDTO.platform() == Platform.APPLE) {
            if (userSignupDTO.authCode() == null || userSignupDTO.authCode().isBlank()) {
                throw new AuthException(AuthErrorMessage.EMPTY_AUTH_CODE);
            }
            appleService.exchangeAndStoreRefreshToken(userSignupDTO.authCode(), user.getUserId());
        }

        JwtTokenDTO token = jwtTokenProvider.generateTokenPair(user.getUserId());
        tokenPort.saveToken(user.getUserId(), token);
        return UserTokenDTO.of(user, token);
    }

    @Override
    public LoginResponseDTO login(PlatformRequestDTO platformRequestDTO, String platformToken) {
        PlatformUserDTO platformUserDTO = getPlatformInfo(platformRequestDTO.platform(), platformToken);
        User user = userPort.load(platformRequestDTO.platform(), platformUserDTO);

        if (user == null) {
            System.out.println("User is null");
            return LoginResponseDTO.of(false, null, null);
        }

        if (platformRequestDTO.platform() == Platform.APPLE
                && appleRefreshTokenPort.findRefreshTokenByUserId(user.getUserId()).isEmpty()) {
            if (platformRequestDTO.authCode() == null || platformRequestDTO.authCode().isBlank()) {
                throw new AuthException(AuthErrorMessage.EMPTY_AUTH_CODE);
            }
            appleService.exchangeAndStoreRefreshToken(platformRequestDTO.authCode(), user.getUserId());
        }

        JwtTokenDTO token = jwtTokenProvider.generateTokenPair(user.getUserId());
        tokenPort.saveToken(user.getUserId(), token);

        return LoginResponseDTO.of(true, user, token);
    }

    @Override
    public void logout(Long userId) {
        tokenPort.deleteRefreshToken(userId);
    }

    @Override
    public void withdraw(Long userId) {
        User user = userPort.findUserById(userId);
        if(user.getPlatform() == Platform.KAKAO) {
            kakaoService.unlink(user.getPlatformId());
        } else if(user.getPlatform() == Platform.APPLE) {
            appleService.revokeByUserId(userId);
        } else {
            throw new AuthException(AuthErrorMessage.PLATFORM_NOT_FOUND);
        }

        tokenPort.deleteRefreshToken(userId);
        userPort.deleteUser(userId);
    }

    @Override
    public JwtTokenDTO refreshAccessToken(final String refreshToken) {
        jwtTokenValidator.validateRefreshToken(refreshToken);
        Long userId = jwtTokenProvider.getClaimFromToken(refreshToken).userId();
        boolean isAccessToken = jwtTokenProvider.getClaimFromToken(refreshToken).isAccessToken();
        tokenPort.checkRefreshToken(refreshToken, userId, isAccessToken);

        // 현재 refresh token 정보 삭제 (Refresh Token Rotation)
        tokenPort.deleteRefreshToken(userId);

        JwtTokenDTO tokens = jwtTokenProvider.generateTokenPair(userId);
        tokenPort.saveToken(userId, tokens);

        return tokens;
    }

    // 회원가입
    private PlatformUserDTO getPlatformInfo(String platformToken, UserSignupDTO userSignupDto) {
        if (userSignupDto.platform().toString().equals("KAKAO")){
            return kakaoService.getPlatformUserInfo(platformToken);
        } else if (userSignupDto.platform().toString().equals("APPLE")){
            return appleService.getPlatformUserInfo(platformToken);
        } else {
            throw new AuthException(AuthErrorMessage.PLATFORM_NOT_FOUND);
        }
    }

    // 로그인
    private PlatformUserDTO getPlatformInfo(Platform platform, String platformToken) {
        if (platform.toString().equals("KAKAO")){
            return kakaoService.getPlatformUserInfo(platformToken);
        } else if (platform.toString().equals("APPLE")){
            return appleService.getPlatformUserInfo(platformToken);
        } else {
            throw new AuthException(AuthErrorMessage.PLATFORM_NOT_FOUND);
        }
    }
}
