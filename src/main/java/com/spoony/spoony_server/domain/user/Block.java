package com.spoony.spoony_server.domain.user;

import com.spoony.spoony_server.adapter.out.persistence.block.db.BlockStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Block {
    private Long blockId;
    private User blocker;
    private User blocked;
    private BlockStatus status;
}
