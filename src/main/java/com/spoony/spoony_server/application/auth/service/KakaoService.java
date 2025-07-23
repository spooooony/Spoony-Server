package com.spoony.spoony_server.application.auth.service;

import com.spoony.spoony_server.adapter.auth.dto.PlatformUserDTO;
import com.spoony.spoony_server.adapter.auth.dto.verification.kakao.KakaoUserDTO;
import com.spoony.spoony_server.adapter.auth.out.external.KakaoFeignClient;
import com.spoony.spoony_server.global.auth.constant.AuthConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {

    @Value("${oauth.kakao.admin-key}")
    private String adminKey;

    private final KakaoFeignClient kakaoFeignClient;

    public PlatformUserDTO getPlatformUserInfo(String platformToken) {
        KakaoUserDTO kakaoUserDTO = kakaoFeignClient.getUserInformation(platformToken);
        System.out.println("KakaoService.getPlatformUserInfo");
        log.info("kakao user info = {}", kakaoUserDTO);
        return PlatformUserDTO.of(
                kakaoUserDTO.id().toString());
    }

    public void unlink(final String platformId) {
        System.out.println("KakaoService.unlink");
        System.out.println("platformId = " + platformId);
        System.out.println(AuthConstant.GRANT_TYPE + adminKey + " " + AuthConstant.TARGET_ID_TYPE + " " + Long.valueOf(platformId));
        kakaoFeignClient.unlinkUser(
                AuthConstant.GRANT_TYPE + adminKey,
                AuthConstant.TARGET_ID_TYPE,
                Long.valueOf(platformId)
        );
    }
}
