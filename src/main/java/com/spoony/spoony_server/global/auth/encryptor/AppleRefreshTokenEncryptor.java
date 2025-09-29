package com.spoony.spoony_server.global.auth.encryptor;

import com.spoony.spoony_server.global.exception.AuthException;
import com.spoony.spoony_server.global.message.auth.AuthErrorMessage;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
public class AppleRefreshTokenEncryptor implements AttributeConverter<String, String> {

    private static final String SCHEME_PREFIX = "v1:";
    private static final String AAD = "apple-refresh";
    private static final int IV_LEN = 12;
    private static final int TAG_LEN_BITS = 128;

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64E = Base64.getEncoder();
    private static final Base64.Decoder B64D = Base64.getDecoder();

    private static volatile SecretKey KEY;

    // Base64 키 문자열로부터 SecretKey 세팅
    private static void setKeyBase64(String keyB64) {
        byte[] raw;
        try {
            raw = B64D.decode(keyB64);
        } catch (Exception e) {
            throw new AuthException(AuthErrorMessage.INVALID_SECRET_KEY);
        }

        KEY = new SecretKeySpec(raw, "AES");
    }

    @Override
    public String convertToDatabaseColumn(String plain) {
        try {
            byte[] iv = new byte[IV_LEN];
            RNG.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, KEY, new GCMParameterSpec(TAG_LEN_BITS, iv));
            cipher.updateAAD(AAD.getBytes(StandardCharsets.UTF_8));

            byte[] ct = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            byte[] out = ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array();
            return SCHEME_PREFIX + B64E.encodeToString(out);
        } catch (Exception e) {
            throw new AuthException(AuthErrorMessage.ENCRYPT_FAILED);
        }
    }

    @Override
    public String convertToEntityAttribute(String db) {
        if (!db.startsWith(SCHEME_PREFIX)) {
            return db;
        }

        try {
            byte[] all = B64D.decode(db.substring(SCHEME_PREFIX.length()));
            if (all.length < IV_LEN + 16) {
                throw new AuthException(AuthErrorMessage.INVALID_CIPHERTEXT);
            }
            byte[] iv = new byte[IV_LEN];
            System.arraycopy(all, 0, iv, 0, IV_LEN);
            byte[] ct = new byte[all.length - IV_LEN];
            System.arraycopy(all, IV_LEN, ct, 0, ct.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, KEY, new GCMParameterSpec(TAG_LEN_BITS, iv));
            cipher.updateAAD(AAD.getBytes(StandardCharsets.UTF_8));

            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new AuthException(AuthErrorMessage.DECRYPT_FAILED);
        }
    }

    @Component
    public static class KeyLoader {

        @Value("${security.token.key-base64}")
        private String keyBase64;

        @PostConstruct
        public void init() {
            AppleRefreshTokenEncryptor.setKeyBase64(keyBase64);
        }
    }
}
