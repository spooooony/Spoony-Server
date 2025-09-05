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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Override
    public void addZzimPost(ZzimAddCommand command) {
        Post post = postPort.findPostById(command.getPostId());
        User user = userPort.findUserById(command.getUserId());
        zzimPostPort.saveZzimPost(user, post);
    }

    @Override
    public ZzimCardListResponseDTO getZzimCardList(ZzimGetCardCommand command) {
        List<ZzimPost> zzimPostList =
            zzimPostPort.findZzimPostsByUserIdAndCategoryIdSortedByCreatedAtDesc(
                command.getUserId(),
                command.getCategoryId()
            );

        // 차단 + 신고 관계 유저 ID (양방향 필터링)
        Set<Long> excludedUserIds = collectExcludedUserIds(command.getUserId());

        // 중복 placeId 제거 + 제외 유저 필터링
        Map<Long, ZzimPost> uniquePlacePostMap = new LinkedHashMap<>();
        for (ZzimPost zzimPost : zzimPostList) {
            Post post = zzimPost.getPost();
            Place place = post.getPlace();
            if (place == null) continue;

            Long authorId = post.getUser().getUserId();
            if (excludedUserIds.contains(authorId)) continue;

            uniquePlacePostMap.putIfAbsent(place.getPlaceId(), zzimPost);
        }

        List<ZzimCardResponseDTO> responseList = uniquePlacePostMap.values().stream()
            .map(this::toZzimCardResponse)
            .toList();

        return ZzimCardListResponseDTO.of(responseList.size(), responseList);
    }

    @Override
    public ZzimFocusListResponseDTO getZzimFocusList(ZzimGetFocusCommand command) {
        User user = userPort.findUserById(command.getUserId());

        // 차단 + 신고 관계 유저 ID
        Set<Long> excludedUserIds = collectExcludedUserIds(user.getUserId());

        List<ZzimPost> zzimPostList =
            zzimPostPort.findZzimPostsByUserIdSortedByCreatedAtDesc(user.getUserId());

        List<ZzimFocusResponseDTO> responseList = zzimPostList.stream()
            .filter(zzimPost -> {
                Post post = zzimPost.getPost();
                Place place = post.getPlace();
                if (place == null) return false;

                Long authorId = post.getUser().getUserId();
                return place.getPlaceId().equals(command.getPlaceId())
                    && !excludedUserIds.contains(authorId);
            })
            .map(this::toZzimFocusResponse)
            .toList();

        return ZzimFocusListResponseDTO.of(responseList);
    }

    @Override
    public void deleteZzim(ZzimDeleteCommand command) {
        User user = userPort.findUserById(command.getUserId());
        Post post = postPort.findPostById(command.getPostId());
        zzimPostPort.deleteByUserAndPost(user, post);
    }

    @Override
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

    // ================== Private Helper Methods ==================

    private Set<Long> collectExcludedUserIds(Long userId) {
        Set<Long> excluded = new HashSet<>();
        excluded.addAll(blockPort.getBlockedUserIds(userId));
        excluded.addAll(blockPort.getBlockerUserIds(userId));
        excluded.addAll(blockPort.getRelatedUserIdsByReportStatus(userId));
        return excluded;
    }

    private Map<Long, ZzimPost> getFilteredUniqueZzimPosts(Long userId) {
        Set<Long> excludedUserIds = collectExcludedUserIds(userId);

        List<ZzimPost> zzimPostList = zzimPostPort.findZzimPostsByUserIdSortedByCreatedAtDesc(userId);

        Map<Long, ZzimPost> uniqueMap = new LinkedHashMap<>();
        for (ZzimPost zzimPost : zzimPostList) {
            Post post = zzimPost.getPost();
            Place place = post.getPlace();
            if (place == null) continue;

            Long authorId = post.getUser().getUserId();
            if (excludedUserIds.contains(authorId)) continue;

            uniqueMap.putIfAbsent(place.getPlaceId(), zzimPost); // 최신순 → 첫 번째만 남김
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

    private ZzimCardListResponseDTO getZzimByAreaDong(Long userId, Double lon, Double lat) {
        Map<Long, ZzimPost> uniqueZzimPosts = getFilteredUniqueZzimPosts(userId);

        List<ZzimCardResponseDTO> responseList = uniqueZzimPosts.values().stream()
            .filter(zzimPost -> {
                Place place = zzimPost.getPost().getPlace();
                return place.getLatitude() != null && place.getLongitude() != null &&
                    calculateDistance(lat, lon, place.getLatitude(), place.getLongitude()) <= 2.0;
            })
            .map(this::toZzimCardResponse)
            .toList();

        return ZzimCardListResponseDTO.of(responseList.size(), responseList);
    }

    private ZzimCardListResponseDTO getZzimByAreaStation(Long userId, Double lon, Double lat) {
        Map<Long, ZzimPost> uniqueZzimPosts = getFilteredUniqueZzimPosts(userId);

        List<ZzimCardResponseDTO> responseList = uniqueZzimPosts.values().stream()
            .filter(zzimPost -> {
                Place place = zzimPost.getPost().getPlace();
                return place.getLatitude() != null && place.getLongitude() != null &&
                    calculateDistance(lat, lon, place.getLatitude(), place.getLongitude()) <= 1.0;
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
            postCategory.getCategory().getBackgroundColor()
        );

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

    private ZzimFocusResponseDTO toZzimFocusResponse(ZzimPost zzimPost) {
        Post post = zzimPost.getPost();
        Place place = post.getPlace();
        PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());

        CategoryColorResponseDTO categoryColor = CategoryColorResponseDTO.of(
            postCategory.getCategory().getCategoryId(),
            postCategory.getCategory().getCategoryName(),
            postCategory.getCategory().getIconUrlColor(),
            postCategory.getCategory().getTextColor(),
            postCategory.getCategory().getBackgroundColor()
        );

        List<String> photoUrlList = zzimPostPort.findPhotoListById(post.getPostId()).stream()
            .map(Photo::getPhotoUrl)
            .toList();

        String regionName = post.getUser().getRegion() != null
            ? post.getUser().getRegion().getRegionName()
            : null;

        return ZzimFocusResponseDTO.of(
            place.getPlaceId(),
            place.getPlaceName(),
            categoryColor,
            post.getUser().getUserName(),
            regionName,
            post.getPostId(),
            post.getDescription(),
            post.getZzimCount(),
            photoUrlList
        );
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return EARTH_RADIUS * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }
}
