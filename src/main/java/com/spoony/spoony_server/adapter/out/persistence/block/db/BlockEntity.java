package com.spoony.spoony_server.adapter.out.persistence.block.db;


import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public BlockEntity(Long blockId,UserEntity blocker,UserEntity blocked){
        this.blockId = blockId;
        this.blocker = blocker;
        this.blocked = blocked;
    }
}
