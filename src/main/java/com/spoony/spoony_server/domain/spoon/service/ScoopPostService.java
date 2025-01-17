package com.spoony.spoony_server.domain.spoon.service;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.SpoonErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ScoopPostEntity;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.spoon.dto.request.ScoopPostRequestDTO;
import com.spoony.spoony_server.domain.spoon.entity.ActivityEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonBalanceEntity;
import com.spoony.spoony_server.domain.spoon.entity.SpoonHistoryEntity;
import com.spoony.spoony_server.domain.spoon.repository.ActivityRepository;
import com.spoony.spoony_server.domain.spoon.repository.ScoopPostRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonBalanceRepository;
import com.spoony.spoony_server.domain.spoon.repository.SpoonHistoryRepository;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScoopPostService {

    private final ScoopPostRepository scoopPostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final SpoonBalanceRepository spoonBalanceRepository;
    private final SpoonHistoryRepository spoonHistoryRepository;


    @Transactional
    public ResponseEntity<ResponseDTO<Void>> ScoopPost(ScoopPostRequestDTO scoopPostRequestDTO) {

        Long postId = scoopPostRequestDTO.postId();
        Long userId = scoopPostRequestDTO.userId();

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));


//
//        PlaceEntity placeEntity = PlaceEntity.builder()
//                .placeName(postCreateDTO.placeName())
//                .placeAddress(postCreateDTO.placeAddress())
//                .placeRoadAddress(postCreateDTO.placeRoadAddress())
//                .latitude(postCreateDTO.latitude())
//                .longitude(postCreateDTO.longitude())
//                .build();
//
//        placeRepository.save(placeEntity);

//        @Builder
//    public ScoopPostEntity(Long scoopId, UserEntity user, PostEntity post) {
//            this.scoopId = scoopId;
//            this.user = user;
//            this.post = post;
//        }
        //떠먹은 포스트에 반영
        ScoopPostEntity scoopPostEntity = ScoopPostEntity.builder().user(userEntity).post(postEntity).build();
        scoopPostRepository.save(scoopPostEntity);

        // 작성자 스푼 개수 조정
        ActivityEntity activityEntity = activityRepository.findById(3L)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.ACTIVITY_NOT_FOUND));

        SpoonBalanceEntity spoonBalanceEntity = spoonBalanceRepository.findByUser(userEntity)
                .orElseThrow(() -> new BusinessException(SpoonErrorMessage.USER_NOT_FOUND));

        spoonBalanceEntity.setAmount(spoonBalanceEntity.getAmount() + activityEntity.getChangeAmount());
        spoonBalanceEntity.setUpdatedAt(LocalDateTime.now());

        spoonBalanceRepository.save(spoonBalanceEntity);

        // 스푼 히스토리 기록
        SpoonHistoryEntity spoonHistoryEntity = SpoonHistoryEntity.builder()
                .user(userEntity)
                .activity(activityEntity)
                .balanceAfter(spoonBalanceEntity.getAmount())
                .createdAt(LocalDateTime.now())
                .build();

        spoonHistoryRepository.save(spoonHistoryEntity);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));

    }
}
