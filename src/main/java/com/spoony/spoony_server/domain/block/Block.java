package com.spoony.spoony_server.domain.block;

import com.spoony.spoony_server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Block {
    private Long blockId;
    private User blocker;
    private User blocked;
}
