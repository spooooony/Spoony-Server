package com.spoony.spoony_server.adapter.out.persistence.admin;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.mapper.PostMapper;
import com.spoony.spoony_server.application.port.out.admin.AdminPostPort;
import com.spoony.spoony_server.domain.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminPostPersistenceAdapter implements AdminPostPort {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public void softDelete(Long postId) {
        PostEntity entity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        entity.markDeleted();
        postRepository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Long postId) {
        PostEntity entity = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
        entity.restore();
        postRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findDeleted(int page, int size) {
        PageRequest pageable = PageRequest.of(page - 1, size);
        return postRepository.findDeletedPosts(pageable)
                .stream()
                .map(PostMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int countDeletedPosts() {
        return (int) postRepository.countDeletedPosts();
    }
}
