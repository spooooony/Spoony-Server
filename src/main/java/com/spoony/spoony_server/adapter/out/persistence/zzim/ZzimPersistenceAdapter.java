package com.spoony.spoony_server.adapter.out.persistence.zzim;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPort;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ZzimPersistenceAdapter implements ZzimPort {

    private final ZzimPostRepository zzimPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public Long countZzimByPostId(Long postId) {
        return zzimPostRepository.countByPost_PostId(postId);
    }

    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return zzimPostRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Override
    public List<ZzimPostEntity> findByUser(UserEntity userEntity) {
        return zzimPostRepository.findByUser(userEntity);
    }

    @Override
    public void saveZzimPost(User user, Post post) {
        UserEntity userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        PostEntity postEntity = postRepository.findById(post.getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        ZzimPostEntity zzimPostEntity = ZzimPostEntity.builder()
                .user(userEntity)
                .post(postEntity)
                .build();

        zzimPostRepository.save(zzimPostEntity);
    }
}
