package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.verification.kakao.KakaoUserDTO;
import com.spoony.spoony_server.adapter.auth.out.external.KakaoFeignClient;
import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${oauth.kakao.admin-key}")
    private String adminKey;

    private final KakaoFeignClient kakaoFeignClient;

    public PlatformUserDTO getPlatformUserInfo(String platformToken) {
        KakaoUserDTO kakaoUserDTO = kakaoFeignClient.getUserInformation(AuthConstant.BEARER_TOKEN_PREFIX + platformToken);
        return PlatformUserDTO.of(
                kakaoUserDTO.id().toString(),
                kakaoUserDTO.kakaoAccount().email());
    }

    public void unlink(final String platformId) {
        kakaoFeignClient.unlinkUser(
                AuthConstant.GRANT_TYPE + adminKey,
                AuthConstant.TARGET_ID_TYPE,
                Long.valueOf(platformId)
        );
    }
}
