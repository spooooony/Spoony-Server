package com.spoony.spoony_server.adapter.out.persistence.admin;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.mapper.PostMapper;
import com.spoony.spoony_server.application.port.out.admin.AdminPostPort;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PostErrorMessage;
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
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        entity.markDeleted();
        postRepository.save(entity);
    }

    @Override
    @Transactional
    public void restore(Long postId) {
        PostEntity entity = postRepository.findByIdIncludingDeleted(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        entity.restore();
    }

    @Override
    @Transactional
    public void physicalDelete(Long postId) {
        // native delete 사용하면 JPA cascade 적용이 안되기 때문에 복구 후 삭제
        PostEntity entity = postRepository.findByIdIncludingDeleted(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        entity.restore();

        // JPA remove
        postRepository.delete(entity);
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
