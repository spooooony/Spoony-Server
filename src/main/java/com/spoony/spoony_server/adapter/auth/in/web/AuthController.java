package com.spoony.spoony_server.adapter.auth.in.web;

import com.spoony.spoony_server.adapter.auth.dto.request.UserLoginDTO;
import com.spoony.spoony_server.adapter.auth.dto.response.UserTokenDTO;
import com.spoony.spoony_server.application.auth.port.in.SignInUseCase;
import com.spoony.spoony_server.global.constant.AuthConstant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SignInUseCase signInUseCase;

    @PostMapping("/v1/auth/signin")
    public ResponseEntity<UserTokenDTO> signIn(
            @NotBlank @RequestHeader(AuthConstant.AUTHORIZATION_HEADER) final String providerToken,
            @Valid @RequestBody final UserLoginDTO userLoginDTO
    ) {
        return ResponseEntity.ok(signInUseCase.signIn(providerToken, userLoginDTO));
    }
}
