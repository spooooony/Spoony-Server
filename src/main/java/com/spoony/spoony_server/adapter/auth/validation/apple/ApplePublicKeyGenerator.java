package com.spoony.spoony_server.adapter.auth.validation.apple;

import com.spoony.spoony_server.adapter.auth.dto.validation.apple.ApplePublicKeyDTO;
import com.spoony.spoony_server.adapter.auth.dto.validation.apple.ApplePublicKeyListDTO;
import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class ApplePublicKeyGenerator {

    private static final String SIGN_ALGORITHM_HEADER_KEY = "alg";
    private static final String KEY_ID_HEADER_KEY = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;

    public PublicKey generatePublicKey(Map<String, String> headers, ApplePublicKeyListDTO applePublicKeyListDTO) {
        ApplePublicKeyDTO applePublicKeyDTO = applePublicKeyListDTO.getMatchesKey(
                headers.get(SIGN_ALGORITHM_HEADER_KEY),
                headers.get(KEY_ID_HEADER_KEY));

        return generatePublicKeyWithApplePublicKey(applePublicKeyDTO);
    }

    private PublicKey generatePublicKeyWithApplePublicKey(ApplePublicKeyDTO applePublicKeyDTO) {
        byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKeyDTO.n());
        byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKeyDTO.e());

        BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(applePublicKeyDTO.kty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new AuthException(AuthErrorMessage.CREATE_PUBLIC_KEY_EXCEPTION);
        }
    }
}
