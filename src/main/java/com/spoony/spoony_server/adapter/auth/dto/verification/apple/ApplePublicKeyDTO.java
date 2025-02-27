package com.spoony.spoony_server.adapter.auth.dto.verification.apple;

public record ApplePublicKeyDTO(String kty,
                                String kid,
                                String use,
                                String alg,
                                String n,
                                String e,
                                String email) {
}
