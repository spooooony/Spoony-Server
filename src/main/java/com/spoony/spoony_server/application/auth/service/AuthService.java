package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.mapper.UserMapper;
import com.spoony.spoony_server.application.auth.port.in.RefreshUseCase;
import com.spoony.spoony_server.application.auth.port.in.SignInUseCase;
import com.spoony.spoony_server.application.auth.port.out.TokenPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
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
        SignInUseCase,
        RefreshUseCase {

    private final UserPort userPort;
    private final TokenPort tokenPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final AppleService appleService;
    private final KakaoService kakaoService;

    @Override
    public UserTokenDTO signIn(String platformToken, UserLoginDTO userLoginDTO) {

         PlatformUserDTO platformUserDTO = getPlatformInfo(platformToken, userLoginDTO);

//         소셜 로그인 테스트 전, 회원가입 테스트 코드
//         PlatformUserDTO platformUserDTO = new PlatformUserDTO("test_id", "test_email");

        User user = userPort.loadOrCreate(platformUserDTO, userLoginDTO);
        JwtTokenDTO token = jwtTokenProvider.generateTokenPair(user.getUserId());
        tokenPort.saveToken(user.getUserId(), token);
        return UserTokenDTO.of(user, token);
    }

    @Override
    @Transactional
    public JwtTokenDTO refreshAccessToken(final String refreshToken) {
        jwtTokenValidator.validateRefreshToken(refreshToken);
        Long userId = jwtTokenProvider.getClaimFromToken(refreshToken).userId();
        boolean isAccessToken = jwtTokenProvider.getClaimFromToken(refreshToken).isAccessToken();
        tokenPort.checkRefreshToken(refreshToken, userId, isAccessToken);

        // 현재 refresh token 정보 삭제 (Refresh Token Rotation)
        tokenPort.deleteRefreshToken(refreshToken);

        JwtTokenDTO tokens = jwtTokenProvider.generateTokenPair(userId);
        tokenPort.saveToken(userId, tokens);

        return tokens;
    }

    private PlatformUserDTO getPlatformInfo(String platformToken, UserLoginDTO userLoginDto) {
        if (userLoginDto.platform().toString().equals("KAKAO")){
            return kakaoService.getPlatformUserInfo(platformToken);
        } else if (userLoginDto.platform().toString().equals("APPLE")){
            return appleService.getPlatformUserInfo(platformToken);
        } else {
            throw new AuthException(AuthErrorMessage.PLATFORM_NOT_FOUND);
        }
    }
}
