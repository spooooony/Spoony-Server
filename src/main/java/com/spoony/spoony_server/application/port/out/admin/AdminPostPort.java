package com.spoony.spoony_server.application.port.out.admin;

import com.spoony.spoony_server.domain.post.Post;

import java.util.List;

public interface AdminPostPort {
    void softDelete(Long postId);   // 논리 삭제
    void restore(Long postId); // 복구
    void physicalDelete(Long postId); // 물리 삭제
    List<Post> findDeleted(int page, int size); // 삭제된 게시글 조회
    int countDeletedPosts();
}
