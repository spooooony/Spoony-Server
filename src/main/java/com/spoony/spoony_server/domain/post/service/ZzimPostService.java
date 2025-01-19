package com.spoony.spoony_server.domain.post.service;

import com.spoony.spoony_server.common.dto.ResponseDTO;
import com.spoony.spoony_server.common.exception.BusinessException;
import com.spoony.spoony_server.common.message.PlaceErrorMessage;
import com.spoony.spoony_server.common.message.PostErrorMessage;
import com.spoony.spoony_server.common.message.UserErrorMessage;
import com.spoony.spoony_server.domain.location.entity.LocationEntity;
import com.spoony.spoony_server.domain.location.repository.LocationRepository;
import com.spoony.spoony_server.domain.place.entity.PlaceEntity;
import com.spoony.spoony_server.domain.place.repository.PlaceRepository;
import com.spoony.spoony_server.domain.post.dto.request.ZzimPostAddRequestDTO;
import com.spoony.spoony_server.domain.post.dto.response.*;
import com.spoony.spoony_server.domain.post.entity.PhotoEntity;
import com.spoony.spoony_server.domain.post.entity.PostCategoryEntity;
import com.spoony.spoony_server.domain.post.entity.PostEntity;
import com.spoony.spoony_server.domain.post.entity.ZzimPostEntity;
import com.spoony.spoony_server.domain.post.repository.PhotoRepository;
import com.spoony.spoony_server.domain.post.repository.PostCategoryRepository;
import com.spoony.spoony_server.domain.post.repository.PostRepository;
import com.spoony.spoony_server.domain.post.repository.ZzimPostRepository;
import com.spoony.spoony_server.domain.user.entity.UserEntity;
import com.spoony.spoony_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final LocationRepository locationRepository;

    public ResponseEntity<ResponseDTO<Void>> addZzimPost(ZzimPostAddRequestDTO zzimPostAddRequest) {

        Long postId = zzimPostAddRequest.postId();
        Long userId = zzimPostAddRequest.userId();

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

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
                                    categoryEntity.getTextColor(),
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
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
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
                                    categoryEntity.getTextColor(),
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

    @Transactional
    public void deleteZzim(Long userId, Long postId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        zzimPostRepository.deleteByUserAndPost(userEntity, postEntity);
    }

    public ZzimCardListResponseDTO getZzimByLocation(Long userId, Long locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new BusinessException(PostErrorMessage.LOCATION_NOT_FOUND));

        if (locationEntity.getLocationTypeEntity().getLocationTypeId() == 3) {
            return getZzimByAddress(userId, locationEntity.getLocationAddress());
        } else {
            return getZzimByArea(userId, locationEntity.getLongitude(), locationEntity.getLatitude());
        }
    }

    private ZzimCardListResponseDTO getZzimByAddress(Long userId, String locationAddress) {
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

        // placeAddress에 locationAddress 문자열이 포함된 데이터만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPostEntity -> {
                    PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
                    return placeEntity.getPlaceAddress() != null && placeEntity.getPlaceAddress().contains(locationAddress);
                })
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity placeEntity = postEntity.getPlace();

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
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

    private ZzimCardListResponseDTO getZzimByArea(Long userId, Double longitude, Double latitude) {
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

        // 주어진 위도(latitude)와 경도(longitude) 기준으로 10km 이내 장소만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPostEntity -> {
                    PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
                    if (placeEntity.getLatitude() == null || placeEntity.getLongitude() == null) {
                        return false;
                    }
                    return calculateDistance(latitude, longitude, placeEntity.getLatitude(), placeEntity.getLongitude()) <= 10.0;
                })
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity placeEntity = postEntity.getPlace();

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
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

    // 두 좌표 간의 거리를 계산하는 메서드 (단위: km)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // 지구 반지름 (단위: km)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
