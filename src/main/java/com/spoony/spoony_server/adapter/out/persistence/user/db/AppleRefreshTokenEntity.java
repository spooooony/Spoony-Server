package com.spoony.spoony_server.adapter.out.persistence.user.db;

import com.spoony.spoony_server.global.auth.encryptor.AppleRefreshTokenEncryptor;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "apple_refresh_token")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AppleRefreshTokenEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Convert(converter = AppleRefreshTokenEncryptor.class)
    @Column(name = "refresh_token", nullable = false, length = 2048)
    private String refreshToken;

    @CreatedDate
    private LocalDateTime createdAt;
}
