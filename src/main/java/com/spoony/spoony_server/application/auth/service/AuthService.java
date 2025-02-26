package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.JwtTokenDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;
import com.spoony.spoony_server.application.auth.port.in.SignInUseCase;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.auth.jwt.JwtTokenProvider;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements SignInUseCase {

    private final UserPort userPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppleService appleService;

    @Override
    public UserTokenDTO signIn(String platformToken, UserLoginDTO userLoginDTO) {
        PlatformUserDTO platformUserDTO = getPlatformInfo(platformToken, userLoginDTO);
        User user = userPort.loadOrCreate(userLoginDTO.platform(), platformUserDTO);
        JwtTokenDTO token = jwtTokenProvider.issueTokens(user.getUserId());
        saveToken(user.getUserId(), token);
        return UserTokenDTO.of(user, token);
    }

    private PlatformUserDTO getPlatformInfo(String platformToken, UserLoginDTO userLoginDto) {
        if (userLoginDto.platform().toString().equals("KAKAO")){
            //TODO
        } else if (userLoginDto.platform().toString().equals("APPLE")){
            return appleService.getPlatformUserInfo(platformToken);
        } else {
            throw new AuthException(AuthErrorMessage.PLATFORM_NOT_FOUND);
        }
        return null;
    }

    private User loadOrCreate(Platform platform, PlatformUserDTO platformUserDTO) {
        //TODO
        return null;
    }

    private void saveToken(Long userId, JwtTokenDTO token) {
        //TODO
    }
}
