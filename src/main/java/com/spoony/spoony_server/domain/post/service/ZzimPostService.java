package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PlaceErrorMessage;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import com.spoony.spoony_server.domain.place.repository.PlaceRepository;
import com.spoony.spoony_server.domain.post.dto.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.domain.post.dto.response.*;
import com.spoony.spoony_server.domain.post.entity.PostCategoryEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import com.spoony.spoony_server.domain.post.repository.PhotoRepository;
import com.spoony.spoony_server.domain.post.repository.PostCategoryRepository;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.post.repository.ZzimPostRepository;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import com.spoony.spoony_server.domain.post.entity.PhotoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZzimPostService {

    private final ZzimPostRepository zzimPostRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PlaceRepository placeRepository;

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
    public ZzimCardListResponseDTO getZzimCardList(Long userId) {
        List<ZzimPostEntity> zzimPostEntityList = zzimPostRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.ZZIM_NOT_FOUND));

        Map<Long, ZzimPostEntity> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPostEntity zzimPostEntity : zzimPostEntityList) {
            PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
            if (placeEntity == null) {
                throw new BusinessException(PostErrorMessage.PLACE_NOT_FOUND);
            }

            Long placeId = placeEntity.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPostEntity);
            }
        }

        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity placeEntity = postEntity.getPlace();

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getBackgroundColor()
                            ))
                            .orElse(null);

                    return new ZzimCardResponseDTO(
                            placeEntity.getPlaceId(),  // placeId 추가
                            placeEntity.getPlaceName(),
                            placeEntity.getPlaceAddress(),
                            postEntity.getTitle(),
                            placeEntity.getLatitude(),
                            placeEntity.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return new ZzimCardListResponseDTO(zzimCardResponses);
    }

    public ZzimFocusListResponseDTO getZzimFocusList(Long userId, Long placeId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.NOT_FOUND_ERROR));
        PlaceEntity targetPlaceEntity = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND));

        List<ZzimPostEntity> zzimPostEntityList = zzimPostRepository.findByUser(userEntity);

        List<ZzimFocusResponseDTO> zzimFocusResponseList = zzimPostEntityList.stream()
                .filter(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity postPlaceEntity = postEntity.getPlace();
                    return postPlaceEntity != null && postPlaceEntity.getPlaceId().equals(placeId);
                })
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity postPlaceEntity = postEntity.getPlace();

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getBackgroundColor()
                            ))
                            .orElse(null);

                    Long zzimCount = zzimPostRepository.countByPost(postEntity);

                    List<String> photoUrlList = photoRepository.findByPost(postEntity).stream()
                            .map(PhotoEntity::getPhotoUrl)
                            .collect(Collectors.toList());

                    return new ZzimFocusResponseDTO(
                            postPlaceEntity.getPlaceId(),
                            postPlaceEntity.getPlaceName(),
                            categoryColorResponse,
                            postEntity.getUser().getUserName(),
                            postEntity.getUser().getRegion().getRegionName(),
                            postEntity.getPostId(),
                            postEntity.getTitle(),
                            zzimCount,
                            photoUrlList
                    );
                })
                .collect(Collectors.toList());

        return new ZzimFocusListResponseDTO(zzimFocusResponseList);
    }
}
