package com.spoony.spoony_server.domain.post.service;


import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.post.dto.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.post.repository.ZzimPostRepository;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ZzimPostService {

    private final ZzimPostRepository zzimPostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public ResponseEntity<ResponseDTO<Void>> addZzimPost(ZzimPostAddRequestDTO zzimPostAddRequest) {

        Long postId = zzimPostAddRequest.postId();
        Long userId = zzimPostAddRequest.userId();

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.NOT_FOUND_ERROR));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));

        ZzimPostEntity zzimPostEntity = ZzimPostEntity.builder().post(postEntity).user(userEntity).build();

        zzimPostRepository.save(zzimPostEntity);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));

    }
}
;
