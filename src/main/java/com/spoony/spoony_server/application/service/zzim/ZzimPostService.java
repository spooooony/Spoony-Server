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

    @Override
    public ZzimCardListResponseDTO getZzimCardList(ZzimGetCardCommand command) {

        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserIdAndCategoryIdSortedByCreatedAtDesc(
                command.getUserId(),
                command.getCategoryId()
        );

        // 차단 유저 조회
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(command.getUserId());

        // 중복 placeId 제거 + 차단 유저 필터링
        Map<Long, ZzimPost> uniquePlacePostMap = new LinkedHashMap<>();
        for (ZzimPost zzimPost : zzimPostList) {
            Post post = zzimPost.getPost();
            Place place = post.getPlace();

            if (place == null) {
                throw new BusinessException(PlaceErrorMessage.PLACE_NOT_FOUND);
            }

            Long authorId = post.getUser().getUserId();
            // 차단한 사용자의 게시물은 스킵
            if (blockedUserIds.contains(authorId)) {
                continue;
            }

            Long placeId = place.getPlaceId();
            uniquePlacePostMap.putIfAbsent(placeId, zzimPost);
        }

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

    @Override
    public ZzimFocusListResponseDTO getZzimFocusList(ZzimGetFocusCommand command) {
        User user = userPort.findUserById(command.getUserId());

        // 차단 유저 조회
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(user.getUserId());

        // 북마크 최신순 정렬
        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserIdSortedByCreatedAtDesc(user.getUserId());

        List<ZzimFocusResponseDTO> zzimFocusResponseList = zzimPostList.stream()
                .filter(zzimPost -> {
                    Post post = zzimPost.getPost();
                    Place postPlace = post.getPlace();
                    User writer = post.getUser();
                    // 장소 일치 + 차단 유저 제외
                    return postPlace != null
                            && postPlace.getPlaceId().equals(command.getPlaceId())
                            && !blockedUserIds.contains(writer.getUserId());
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

                    Long zzimCount = post.getZzimCount();

                    List<Photo> photoList = zzimPostPort.findPhotoListById(post.getPostId());
                    List<String> photoUrlList = photoList.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    String regionName = post.getUser().getRegion() != null
                            ? post.getUser().getRegion().getRegionName()
                            : null;

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
        } else {
            return getZzimByAreaStation(command.getUserId(), location.getLongitude(), location.getLatitude());
        }
    }

    private Map<Long, ZzimPost> getFilteredUniqueZzimPosts(Long userId) {
        List<Long> blockedUserIds = blockPort.getBlockedUserIds(userId);

        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserIdSortedByCreatedAtDesc(userId);

        Map<Long, ZzimPost> uniqueMap = new LinkedHashMap<>();
        for (ZzimPost zzimPost : zzimPostList) {
            Post post = zzimPost.getPost();
            Place place = post.getPlace();
            if (place == null) continue;

            Long authorId = post.getUser().getUserId();
            if (blockedUserIds.contains(authorId)) continue;

            Long placeId = place.getPlaceId();
            uniqueMap.putIfAbsent(placeId, zzimPost); // 최신순이므로 첫 번째만 남김
        }

        return uniqueMap;
    }

    private ZzimCardListResponseDTO getZzimByAddress(Long userId, String locationName) {
        Map<Long, ZzimPost> uniqueZzimPosts = getFilteredUniqueZzimPosts(userId);

        List<ZzimCardResponseDTO> responseList = uniqueZzimPosts.values().stream()
                .filter(zzimPost -> {
                    Place place = zzimPost.getPost().getPlace();
                    return place.getPlaceAddress() != null && place.getPlaceAddress().contains(locationName);
                })
                .map(this::toZzimCardResponse)
                .toList();

        return ZzimCardListResponseDTO.of(responseList.size(), responseList);
    }

    private ZzimCardListResponseDTO getZzimByAreaDong(Long userId, Double longitude, Double latitude) {
        Map<Long, ZzimPost> uniqueZzimPosts = getFilteredUniqueZzimPosts(userId);

        List<ZzimCardResponseDTO> responseList = uniqueZzimPosts.values().stream()
                .filter(zzimPost -> {
                    Place place = zzimPost.getPost().getPlace();
                    return place.getLatitude() != null && place.getLongitude() != null &&
                            calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude()) <= 2.0;
                })
                .map(this::toZzimCardResponse)
                .toList();

        return ZzimCardListResponseDTO.of(responseList.size(), responseList);
    }

    private ZzimCardListResponseDTO getZzimByAreaStation(Long userId, Double longitude, Double latitude) {
        Map<Long, ZzimPost> uniqueZzimPosts = getFilteredUniqueZzimPosts(userId);

        List<ZzimCardResponseDTO> responseList = uniqueZzimPosts.values().stream()
                .filter(zzimPost -> {
                    Place place = zzimPost.getPost().getPlace();
                    return place.getLatitude() != null && place.getLongitude() != null &&
                            calculateDistance(latitude, longitude, place.getLatitude(), place.getLongitude()) <= 1.0;
                })
                .map(this::toZzimCardResponse)
                .toList();

        return ZzimCardListResponseDTO.of(responseList.size(), responseList);
    }

    private ZzimCardResponseDTO toZzimCardResponse(ZzimPost zzimPost) {
        Post post = zzimPost.getPost();
        Place place = post.getPlace();
        Photo photo = zzimPostPort.findFistPhotoById(post.getPostId());
        PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());

        CategoryColorResponseDTO categoryColor = CategoryColorResponseDTO.of(
                postCategory.getCategory().getCategoryId(),
                postCategory.getCategory().getCategoryName(),
                postCategory.getCategory().getIconUrlColor(),
                postCategory.getCategory().getTextColor(),
                postCategory.getCategory().getBackgroundColor());

        return ZzimCardResponseDTO.of(
                place.getPlaceId(),
                place.getPlaceName(),
                post.getDescription(),
                place.getPlaceAddress(),
                photo.getPhotoUrl(),
                place.getLatitude(),
                place.getLongitude(),
                categoryColor
        );
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
