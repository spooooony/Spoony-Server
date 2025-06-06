package com.spoony.spoony_server.application.service.zzim;

import com.spoony.spoony_server.adapter.dto.zzim.response.*;
import com.spoony.spoony_server.application.port.command.zzim.*;
import com.spoony.spoony_server.application.port.in.zzim.ZzimAddUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimGetUseCase;
import com.spoony.spoony_server.application.port.in.zzim.ZzimDeleteUseCase;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.location.LocationPort;
import com.spoony.spoony_server.application.port.out.post.PostCategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.location.Location;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.*;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.domain.zzim.ZzimPost;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.PlaceErrorMessage;
import com.spoony.spoony_server.adapter.dto.post.response.CategoryColorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ZzimPostService implements
        ZzimAddUseCase,
        ZzimGetUseCase,
        ZzimDeleteUseCase {

    private final ZzimPostPort zzimPostPort;
    private final PostCategoryPort postCategoryPort;
    private final PostPort postPort;
    private final UserPort userPort;
    private final LocationPort locationPort;
    private final BlockPort blockPort;

    public void addZzimPost(ZzimAddCommand command) {
        Long postId = command.getPostId();
        Long userId = command.getUserId();

        Post post = postPort.findPostById(postId);
        User user = userPort.findUserById(userId);

        zzimPostPort.saveZzimPost(user, post);
    }

    public ZzimCardListResponseDTO getZzimCardList(ZzimGetCardCommand command) {
        Long zzimCardCount = zzimPostPort.countZzimByUserId(command.getUserId());

        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserIdAndCategoryId(
                command.getUserId(),
                command.getCategoryId()
        );

        // 2. 중복 placeId 제거 + 필터링 (필요하면 여기서 차단 등 필터도 적용 가능)
        Map<Long, ZzimPost> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPost zzimPost : zzimPostList) {
            Place place = zzimPost.getPost().getPlace();
            if (place == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }
            Long placeId = place.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPost);
            }
        }

        // 3. size 이상인 경우 결과 자르기
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .map(zzimPost -> {
                    Post post = zzimPost.getPost();
                    Place place = post.getPlace();
                    String description = post.getDescription();
                    Photo photo = zzimPostPort.findFistPhotoById(post.getPostId());
                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());

                    CategoryColorResponseDTO categoryColorResponse = CategoryColorResponseDTO.of(
                            postCategory.getCategory().getCategoryId(),
                            postCategory.getCategory().getCategoryName(),
                            postCategory.getCategory().getIconUrlColor(),
                            postCategory.getCategory().getTextColor(),
                            postCategory.getCategory().getBackgroundColor()
                    );

                    return ZzimCardResponseDTO.of(
                            place.getPlaceId(),
                            place.getPlaceName(),
                            description,
                            place.getPlaceAddress(),
                            photo.getPhotoUrl(),
                            place.getLatitude(),
                            place.getLongitude(),
                            categoryColorResponse
                    );
                })
                .toList();


        return new ZzimCardListResponseDTO(zzimCardResponses.size(), zzimCardResponses);
    }

    public ZzimFocusListResponseDTO getZzimFocusList(ZzimGetFocusCommand command) { //command -> userId, placeId
        User user = userPort.findUserById(command.getUserId()); //로그인 userId
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(user.getUserId());
        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserId(user.getUserId());

        List<ZzimFocusResponseDTO> zzimFocusResponseList = zzimPostList.stream()
                .filter(zzimPost -> {
                    Post post = zzimPost.getPost();
                    Place postPlace = post.getPlace();
                    User writer = post.getUser();
                    return postPlace != null && postPlace.getPlaceId().equals(
                            command.getPlaceId()) && !blockedUserIds.contains(writer.getUserId()
                    );
                })
                .map(zzimPost -> {
                    Post post = zzimPost.getPost();
                    Place postPlace = post.getPlace();
                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    CategoryColorResponseDTO categoryColorResponse = CategoryColorResponseDTO.of(
                            postCategory.getCategory().getCategoryId(),
                            postCategory.getCategory().getCategoryName(),
                            postCategory.getCategory().getIconUrlColor(),
                            postCategory.getCategory().getTextColor(),
                            postCategory.getCategory().getBackgroundColor());
                    Long zzimCount = zzimPostPort.countZzimByPostId(post.getPostId()) - 1L;

                    List<Photo> photoList = zzimPostPort.findPhotoListById(post.getPostId());
                    List<String> photoUrlList = photoList
                            .stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    String regionName = post.getUser().getRegion() != null ? post.getUser().getRegion().getRegionName() : null;

                    return ZzimFocusResponseDTO.of(
                            postPlace.getPlaceId(),
                            postPlace.getPlaceName(),
                            categoryColorResponse,
                            post.getUser().getUserName(),
                            regionName,
                            post.getPostId(),
                            post.getDescription(),
                            zzimCount,
                            photoUrlList
                    );
                })
                .collect(Collectors.toList());

        return ZzimFocusListResponseDTO.of(zzimFocusResponseList);
    }

    public void deleteZzim(ZzimDeleteCommand command) {
        User user = userPort.findUserById(command.getUserId());
        Post post = postPort.findPostById(command.getPostId());
        zzimPostPort.deleteByUserAndPost(user,post);
    }

    public ZzimCardListResponseDTO getZzimByLocation(ZzimGetLocationCardCommand command) {
        Location location = locationPort.findLocationById(command.getLocationId());

        if (location.getLocationType().getLocationTypeId() == 1) {
            return getZzimByAddress(command.getUserId(), location.getLocationName());
        } else if (location.getLocationType().getLocationTypeId() == 2) {
            return getZzimByAreaDong(command.getUserId(), location.getLongitude(), location.getLatitude());
        } else{
            return getZzimByAreaStation(command.getUserId(), location.getLongitude(), location.getLatitude());
        }
    }

    private ZzimCardListResponseDTO getZzimByAddress(Long userId, String locationName) {
        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserId(userId);
        Map<Long, ZzimPost> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPost zzimPost : zzimPostList) {
            Place place = zzimPost.getPost().getPlace();
            if (place == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }

            Long placeId = place.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPost);
            }
        }

        // placeAddress에 locationName 문자열이 포함된 데이터만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPost-> {
                    Place place= zzimPost.getPost().getPlace();
                    return place.getPlaceAddress() != null && place.getPlaceAddress().contains(locationName);
                })
                .map(zzimPost -> {
                    Post post = zzimPost.getPost();
                    String description = post.getDescription();
                    Place place = post.getPlace();
                    Photo photo = zzimPostPort.findFistPhotoById(post.getPostId());

                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());

                    CategoryColorResponseDTO categoryColorResponse = CategoryColorResponseDTO.of(
                            postCategory.getCategory().getCategoryId(),
                            postCategory.getCategory().getCategoryName(),
                            postCategory.getCategory().getIconUrlColor(),
                            postCategory.getCategory().getTextColor(),
                            postCategory.getCategory().getBackgroundColor());

                    return ZzimCardResponseDTO.of(
                            place.getPlaceId(),  // placeId 추가
                            place.getPlaceName(),
                            description,
                            place.getPlaceAddress(),
                            photo.getPhotoUrl(),
                            place.getLatitude(),
                            place.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return ZzimCardListResponseDTO.of(zzimCardResponses.size(), zzimCardResponses);
    }

    private ZzimCardListResponseDTO getZzimByAreaDong(Long userId, Double longitude, Double latitude) {
        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserId(userId);

        Map<Long, ZzimPost> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPost zzimPost : zzimPostList) {
            Place place = zzimPost.getPost().getPlace();
            if (place == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }

            Long placeId = place.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPost);
            }
        }

        // 주어진 위도(latitude)와 경도(longitude) 기준으로 2km 이내 장소만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPost -> {
                    Place place = zzimPost.getPost().getPlace();
                    if (place.getLatitude() == null || place.getLongitude() == null) {
                        return false;
                    }
                    return calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude()) <= 2.0;
                })
                .map(zzimPost -> {
                    Post post = zzimPost.getPost();
                    String description = post.getDescription();
                    Place place = post.getPlace();
                    Photo photo = zzimPostPort.findFistPhotoById(post.getPostId());

                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());

                    CategoryColorResponseDTO categoryColorResponse = CategoryColorResponseDTO.of(
                            postCategory.getCategory().getCategoryId(),
                            postCategory.getCategory().getCategoryName(),
                            postCategory.getCategory().getIconUrlColor(),
                            postCategory.getCategory().getTextColor(),
                            postCategory.getCategory().getBackgroundColor());

                    return ZzimCardResponseDTO.of(
                            place.getPlaceId(),  // placeId 추가
                            place.getPlaceName(),
                            description,
                            place.getPlaceAddress(),
                            photo.getPhotoUrl(),
                            place.getLatitude(),
                            place.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return ZzimCardListResponseDTO.of(zzimCardResponses.size(), zzimCardResponses);
    }

    private ZzimCardListResponseDTO getZzimByAreaStation(Long userId, Double longitude, Double latitude) {
        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserId(userId);

        Map<Long, ZzimPost> uniquePlacePostMap = new LinkedHashMap<>();

        for (ZzimPost zzimPost : zzimPostList) {
            Place place = zzimPost.getPost().getPlace();
            if (place == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }

            Long placeId = place.getPlaceId();
            if (!uniquePlacePostMap.containsKey(placeId)) {
                uniquePlacePostMap.put(placeId, zzimPost);
            }
        }

        // 주어진 위도(latitude)와 경도(longitude) 기준으로 2km 이내 장소만 필터링
        List<ZzimCardResponseDTO> zzimCardResponses = uniquePlacePostMap.values().stream()
                .filter(zzimPost -> {
                    Place place = zzimPost.getPost().getPlace();
                    if (place.getLatitude() == null || place.getLongitude() == null) {
                        return false;
                    }
                    return calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude()) <= 1.0;
                })
                .map(zzimPost -> {
                    Post post = zzimPost.getPost();
                    String description = post.getDescription();
                    Place place = post.getPlace();

                    Photo photo = zzimPostPort.findFistPhotoById(post.getPostId());

                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());

                    CategoryColorResponseDTO categoryColorResponse = CategoryColorResponseDTO.of(
                            postCategory.getCategory().getCategoryId(),
                            postCategory.getCategory().getCategoryName(),
                            postCategory.getCategory().getIconUrlColor(),
                            postCategory.getCategory().getTextColor(),
                            postCategory.getCategory().getBackgroundColor());

                    return ZzimCardResponseDTO.of(
                            place.getPlaceId(),  // placeId 추가
                            place.getPlaceName(),
                            description,
                            place.getPlaceAddress(),
                            photo.getPhotoUrl(),
                            place.getLatitude(),
                            place.getLongitude(),
                            categoryColorResponse
                    );
                })
                .collect(Collectors.toList());

        return ZzimCardListResponseDTO.of(zzimCardResponses.size(), zzimCardResponses);
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
