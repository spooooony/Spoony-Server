package com.spoony.spoony_server.domain.post.service;


import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.post.dto.response.PostResponseDTO;
import com.spoony.spoony_server.domain.post.entity.MenuEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.user.entity.RegionEntity;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;

    }


    public PostResponseDTO getPostById(Integer postId) {

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));
        UserEntity userEntity = userRepository.findUserByPostId(postId).orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR);
        RegionEntity regionEntity = userRepository.findReigonByUserId(userEntity.getUserId()).orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));
        LocalDateTime latestDate = postEntity.getUpdatedAt().isAfter(postEntity.getCreatedAt()) ? postEntity.getUpdatedAt() : postEntity.getCreatedAt();
        String formattedDate = latestDate.toLocalDate().toString();

        MenuEntity menuEntity = postRepository.findMenuByPostId(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));
        return new PostResponseDTO(userEntity.getUserId(), userEntity.getUserName(), regionEntity.getRegionName(), postEntity.getTitle(), formattedDate, menuEntity.getMenuName().toString(), postEntity.getDescription(), postEntity.getPlace(),


//
//         Integer postId,
//    Integer userId,
//    String userName,
//    String userRegion,
//    String Category,
//    String title,
//    String date,
//    List<String> menu,
//    String description,
//    String place_name,
//    String place_address,
//    Double latitude,
//    Double longitude,
//    Integer zzin

    }
}
