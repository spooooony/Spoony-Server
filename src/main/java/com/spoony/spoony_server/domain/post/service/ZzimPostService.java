package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import com.spoony.spoony_server.domain.post.dto.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.domain.post.dto.response.CategoryColorResponseDTO;
import com.spoony.spoony_server.domain.post.dto.response.ZzimCardListResponse;
import com.spoony.spoony_server.domain.post.dto.response.ZzimCardResponse;
import com.spoony.spoony_server.domain.post.entity.PostCategoryEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import com.spoony.spoony_server.domain.post.repository.PostCategoryRepository;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.post.repository.ZzimPostRepository;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZzimPostService {

    private final ZzimPostRepository zzimPostRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ResponseEntity<ResponseDTO<Void>> addZzimPost(ZzimPostAddRequestDTO zzimPostAddRequest) {

        Long postId = zzimPostAddRequest.postId();
        Long userId = zzimPostAddRequest.userId();

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));

        ZzimPostEntity zzimPostEntity = ZzimPostEntity.builder().post(postEntity).user(userEntity).build();

        zzimPostRepository.save(zzimPostEntity);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.success(null));

    }

    //사용자 지도 리스트 조회
    public ZzimCardListResponse getZzimCardList(Long userId) {
        List<ZzimPostEntity> zzimEntityList = zzimPostRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.ZZIM_NOT_FOUND));

        List<ZzimCardResponse> zzimCardResponses = zzimEntityList.stream()
                .map(zzimPost -> {
                    PostEntity post = zzimPost.getPost();
                    PlaceEntity place = post.getPlace();
                    if (place == null) {
                        throw new BusinessException(PostErrorMessage.PLACE_NOT_FOUND);
                    }

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(post)
                            .map(PostCategoryEntity::getCategory)
                            .map(category -> new CategoryColorResponseDTO(
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getBackgroundColor()
                            ))
                            .orElse(null);

                    return new ZzimCardResponse(
                            place.getPlaceName(),
                            place.getPlaceAddress(),
                            post.getTitle(),
                            place.getLatitude(),
                            place.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return new ZzimCardListResponse(zzimCardResponses);
    }
}
