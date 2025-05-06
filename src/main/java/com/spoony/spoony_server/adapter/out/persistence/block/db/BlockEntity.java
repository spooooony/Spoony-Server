package com.spoony.spoony_server.adapter.out.persistence.block.db;


import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "block")
public class BlockEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id")
    private UserEntity blocker;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private UserEntity blocked;

    @Enumerated(EnumType.STRING)
    private BlockStatus status;

    @Column(nullable = false)
    private LocalDateTime time;




    @Builder
    public BlockEntity(UserEntity blocker,UserEntity blocked,BlockStatus status){
        this.blocker = blocker;
        this.blocked = blocked;
        this.status = status;
        this.time = LocalDateTime.now();

    }

    public void updateStatus(BlockStatus status) {
        this.status = status;
        this.time = LocalDateTime.now();
    }
    public BlockEntity changeStatus(BlockStatus status) {
        return BlockEntity.builder()
                .blocker(this.blocker)
                .blocked(this.blocked)
                .status(status)
                .build();
    }
}
