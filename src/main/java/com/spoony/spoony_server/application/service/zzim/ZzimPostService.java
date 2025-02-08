package com.spoony.spoony_server.application.service.zzim;

import com.spoony.spoony_server.application.port.command.zzim.*;
import com.spoony.spoony_server.application.port.in.zzim.ZzimAddUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimGetUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimDeleteUseCase;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.PlaceErrorMessage;
import com.spoony.spoony_server.global.message.PostErrorMessage;
import com.spoony.spoony_server.global.message.UserErrorMessage;
import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationEntity;
import com.spoony.spoony_server.adapter.out.persistence.location.db.LocationRepository;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceRepository;
import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PhotoEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PhotoRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostRepository;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserRepository;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimCardListResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimCardResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimFocusListResponseDTO;
import com.spoony.spoony_server.adapter.dto.zzim.ZzimFocusResponseDTO;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZzimPostService implements
        ZzimAddUseCase,
        ZzimGetUseCase,
        ZzimDeleteUseCase {

    private final ZzimPostRepository zzimPostRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PlaceRepository placeRepository;
    private final LocationRepository locationRepository;

    public void addZzimPost(ZzimAddCommand command) {

        Long postId = command.getPostId();
        Long userId = command.getUserId();

        PostEntity postEntity = postRepository.findById(postId).orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        ZzimPostEntity zzimPostEntity = ZzimPostEntity.builder().post(postEntity).user(userEntity).build();

        zzimPostRepository.save(zzimPostEntity);
    }

    //사용자 지도 리스트 조회
    public ZzimCardListResponseDTO getZzimCardList(ZzimGetCardCommand command) {
        List<ZzimPostEntity> zzimPostEntityList = zzimPostRepository.findByUser(userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND))
        );

        Map<Long, ZzimPostEntity> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPostEntity zzimPostEntity : zzimPostEntityList) {
            PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
            if (placeEntity == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
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

                    PhotoEntity photoEntity = photoRepository.findFirstByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND));

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryId(),
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
                                    categoryEntity.getBackgroundColor()
                            ))
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));

                    return new ZzimCardResponseDTO(
                            placeEntity.getPlaceId(),  // placeId 추가
                            placeEntity.getPlaceName(),
                            placeEntity.getPlaceAddress(),
                            postEntity.getTitle(),
                            photoEntity.getPhotoUrl(),
                            placeEntity.getLatitude(),
                            placeEntity.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return new ZzimCardListResponseDTO(zzimCardResponses.size(), zzimCardResponses);
    }

    public ZzimFocusListResponseDTO getZzimFocusList(ZzimGetFocusCommand command) {
        UserEntity userEntity = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));

        List<ZzimPostEntity> zzimPostEntityList = zzimPostRepository.findByUser(userEntity);

        List<ZzimFocusResponseDTO> zzimFocusResponseList = zzimPostEntityList.stream()
                .filter(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity postPlaceEntity = postEntity.getPlace();
                    return postPlaceEntity != null && postPlaceEntity.getPlaceId().equals(command.getPlaceId());
                })
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity postPlaceEntity = postEntity.getPlace();

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryId(),
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
                                    categoryEntity.getBackgroundColor()
                            ))
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));

                    Long zzimCount = zzimPostRepository.countByPost(postEntity);

                    List<String> photoUrlList = photoRepository.findByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND))
                            .stream()
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
    public void deleteZzim(ZzimDeleteCommand command) {
        UserEntity userEntity = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND));
        PostEntity postEntity = postRepository.findById(command.getPostId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.POST_NOT_FOUND));

        zzimPostRepository.deleteByUserAndPost(userEntity, postEntity);
    }

    public ZzimCardListResponseDTO getZzimByLocation(ZzimGetLocationCardCommand command) {
        LocationEntity locationEntity = locationRepository.findById(command.getLocationId())
                .orElseThrow(() -> new BusinessException(PostErrorMessage.LOCATION_NOT_FOUND));

        if (locationEntity.getLocationTypeEntity().getLocationTypeId() == 1) {
            return getZzimByAddress(command.getUserId(), locationEntity.getLocationName());
        } else if (locationEntity.getLocationTypeEntity().getLocationTypeId() == 2) {
            return getZzimByAreaDong(command.getUserId(), locationEntity.getLongitude(), locationEntity.getLatitude());
        } else{
            return getZzimByAreaStation(command.getUserId(), locationEntity.getLongitude(), locationEntity.getLatitude());
        }
    }

    private ZzimCardListResponseDTO getZzimByAddress(Long userId, String locationName) {
        List<ZzimPostEntity> zzimPostEntityList = zzimPostRepository.findByUser((userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND))
        ));

        Map<Long, ZzimPostEntity> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPostEntity zzimPostEntity : zzimPostEntityList) {
            PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
            if (placeEntity == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }

            Long placeId = placeEntity.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPostEntity);
            }
        }

        // placeAddress에 locationName 문자열이 포함된 데이터만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPostEntity -> {
                    PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
                    return placeEntity.getPlaceAddress() != null && placeEntity.getPlaceAddress().contains(locationName);
                })
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity placeEntity = postEntity.getPlace();

                    PhotoEntity photoEntity = photoRepository.findFirstByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND));

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryId(),
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
                                    categoryEntity.getBackgroundColor()
                            ))
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));

                    return new ZzimCardResponseDTO(
                            placeEntity.getPlaceId(),  // placeId 추가
                            placeEntity.getPlaceName(),
                            placeEntity.getPlaceAddress(),
                            postEntity.getTitle(),
                            photoEntity.getPhotoUrl(),
                            placeEntity.getLatitude(),
                            placeEntity.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return new ZzimCardListResponseDTO(zzimCardResponses.size(), zzimCardResponses);
    }

    private ZzimCardListResponseDTO getZzimByAreaDong(Long userId, Double longitude, Double latitude) {
        List<ZzimPostEntity> zzimPostEntityList = zzimPostRepository.findByUser(userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND))
        );

        Map<Long, ZzimPostEntity> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPostEntity zzimPostEntity : zzimPostEntityList) {
            PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
            if (placeEntity == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }

            Long placeId = placeEntity.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPostEntity);
            }
        }

        // 주어진 위도(latitude)와 경도(longitude) 기준으로 2km 이내 장소만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPostEntity -> {
                    PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
                    if (placeEntity.getLatitude() == null || placeEntity.getLongitude() == null) {
                        return false;
                    }
                    return calculateDistance(latitude, longitude, placeEntity.getLatitude(), placeEntity.getLongitude()) <= 2.0;
                })
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity placeEntity = postEntity.getPlace();

                    PhotoEntity photoEntity = photoRepository.findFirstByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND));

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryId(),
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
                                    categoryEntity.getBackgroundColor()
                            ))
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));

                    return new ZzimCardResponseDTO(
                            placeEntity.getPlaceId(),  // placeId 추가
                            placeEntity.getPlaceName(),
                            placeEntity.getPlaceAddress(),
                            postEntity.getTitle(),
                            photoEntity.getPhotoUrl(),
                            placeEntity.getLatitude(),
                            placeEntity.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return new ZzimCardListResponseDTO(zzimCardResponses.size(), zzimCardResponses);
    }

    private ZzimCardListResponseDTO getZzimByAreaStation(Long userId, Double longitude, Double latitude) {
        List<ZzimPostEntity> zzimPostEntityList = zzimPostRepository.findByUser(userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorMessage.USER_NOT_FOUND))
        );

        Map<Long, ZzimPostEntity> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPostEntity zzimPostEntity : zzimPostEntityList) {
            PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
            if (placeEntity == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }

            Long placeId = placeEntity.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPostEntity);
            }
        }

        // 주어진 위도(latitude)와 경도(longitude) 기준으로 2km 이내 장소만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPostEntity -> {
                    PlaceEntity placeEntity = zzimPostEntity.getPost().getPlace();
                    if (placeEntity.getLatitude() == null || placeEntity.getLongitude() == null) {
                        return false;
                    }
                    return calculateDistance(latitude, longitude, placeEntity.getLatitude(), placeEntity.getLongitude()) <= 1.0;
                })
                .map(zzimPostEntity -> {
                    PostEntity postEntity = zzimPostEntity.getPost();
                    PlaceEntity placeEntity = postEntity.getPlace();

                    PhotoEntity photoEntity = photoRepository.findFirstByPost(postEntity)
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.PHOTO_NOT_FOUND));

                    CategoryColorResponseDTO categoryColorResponse = postCategoryRepository.findByPost(postEntity)
                            .map(PostCategoryEntity::getCategory)
                            .map(categoryEntity -> new CategoryColorResponseDTO(
                                    categoryEntity.getCategoryId(),
                                    categoryEntity.getCategoryName(),
                                    categoryEntity.getIconUrlColor(),
                                    categoryEntity.getTextColor(),
                                    categoryEntity.getBackgroundColor()
                            ))
                            .orElseThrow(() -> new BusinessException(PostErrorMessage.CATEGORY_NOT_FOUND));

                    return new ZzimCardResponseDTO(
                            placeEntity.getPlaceId(),  // placeId 추가
                            placeEntity.getPlaceName(),
                            placeEntity.getPlaceAddress(),
                            postEntity.getTitle(),
                            photoEntity.getPhotoUrl(),
                            placeEntity.getLatitude(),
                            placeEntity.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return new ZzimCardListResponseDTO(zzimCardResponses.size(), zzimCardResponses);
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
