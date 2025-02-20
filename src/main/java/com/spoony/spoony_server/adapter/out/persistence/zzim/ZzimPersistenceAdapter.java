package com.spoony.spoony_server.adapter.out.persistence.zzim;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PhotoRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.mapper.PhotoMapper;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import com.spoony.spoony_server.adapter.out.persistence.zzim.mapper.ZzimMapper;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.domain.zzim.ZzimPost;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ZzimPersistenceAdapter implements ZzimPostPort {

    private final ZzimPostRepository zzimPostRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;

    @Override
    public Long countZzimByPostId(Long postId) {
        return zzimPostRepository.countByPost_PostId(postId);
    }

    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return zzimPostRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }


    @Override
    public List<ZzimPost> findUserByUserId(Long userId) {
        return zzimPostRepository.findByUser_UserId(userId)
                .stream()
                .map(ZzimMapper::toDomain)
                .toList();
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

    @Override
    public Photo findFistPhotoById(Long postId) {
        return photoRepository.findByPost_PostId(postId)
                .map(list -> PhotoMapper.toDomain(list.get(0))) // 첫 번째 요소만 매핑
                .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND));
    }


    @Override
    public List<Photo> findPhotoListById(Long postId) {
        return photoRepository.findByPost_PostId(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND))
                .stream()
                .map(PhotoMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByUserAndPost(User user, Post post) {
        UserEntity userEntity = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        PostEntity postEntity = postRepository.findById(post.getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        zzimPostRepository.deleteByUserAndPost(userEntity, postEntity);
    }

}

