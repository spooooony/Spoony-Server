package com.spoony.spoony_server.adapter.out.persistence.feed;

import com.spoony.spoony_server.adapter.dto.Cursor;
import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.user.AgeGroup;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostSpecification {

    private static final Logger logger = LoggerFactory.getLogger(PostSpecification.class);

    // 카테고리 필터링 (category_id = 1은 단독 사용, category_id = 2는 3~9와 중복 가능)
    public static Specification<PostEntity> withCategoryIds(List<Long> categoryIds) {
        return (root, query, cb) -> {
            Logger logger = LoggerFactory.getLogger(PostSpecification.class);
            logger.debug("카테고리 필터링 시작: categoryIds = {}", categoryIds);

            // categoryIds가 null 또는 비어있으면 전체 조회 (필터 X)
            if (categoryIds == null || categoryIds.isEmpty()) {
                logger.debug("categoryIds가 null 또는 빈 리스트입니다. 필터링 없이 전체 게시글 조회.");
                return cb.conjunction();  // 필터 없음
            }

            // category_id = 1 단독 사용 여부 검사
            if (categoryIds.contains(1L)) {
                if (categoryIds.size() > 1) {
                    throw new IllegalArgumentException("category_id 1은 단독으로만 사용할 수 있습니다.");
                }

                // category_id = 1만 포함되어 있을 경우 ⇒ 전체 조회
                logger.debug("category_id = 1 단독 사용 → 전체 게시글 조회");
                return cb.conjunction();  // 필터 없음
            }

            // 나머지 경우는 categoryIds로 필터링
            Join<PostEntity, PostCategoryEntity> postCategoryJoin = root.join("postCategories");

            CriteriaBuilder.In<Long> inClause = cb.in(postCategoryJoin.get("category").get("categoryId"));
            for (Long id : categoryIds) {
                inClause.value(id);
            }

            logger.debug("카테고리 필터 적용됨: {}", categoryIds);
            return inClause;
        };
    }


    // 로컬리뷰 필터링 (category_id = 2인 경우, 게시물 지역 필터링)
    public static Specification<PostEntity> withLocalReview(List<Long> categoryIds) {

        return (root, query, builder) -> {
            if (categoryIds.contains(2L)) {  // 로컬리뷰만 필터링
                // 작성자 지역과 맛집의 지역이 일치하는 조건 추가
                return builder.equal(root.get("user").get("region").get("regionId"), root.get("place").get("region").get("regionId"));
            }
            return builder.conjunction();  // 로컬리뷰가 아닌 경우엔 필터를 적용하지 않음
        };
    }

    // 지역 필터링 (region_id를 사용하여 해당 지역의 게시물만 가져옴)
    public static Specification<PostEntity> withRegionIds(List<Long> regionIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            logger.debug("지역 필터링 시작: regionIds = {}", regionIds);

            if (regionIds != null && !regionIds.isEmpty()) {
                // place 테이블과 join하여 region_id를 필터링
                Join<PostEntity, PlaceEntity> placeJoin = root.join("place", JoinType.LEFT); // LEFT JOIN을 사용하여 region_id가 null인 경우도 처리

                // region_id가 null인 경우도 포함하는 조건 추가
                Predicate regionPredicate = placeJoin.get("region").get("id").in(regionIds);
                //Predicate nullRegionPredicate = cb.isNull(placeJoin.get("region")); // region_id가 null인 경우

                // 두 조건을 OR로 결합하여 region_id가 null인 경우도 필터링
                predicates.add(regionPredicate);
                logger.debug("지역 id가 {} 또는 region이 null인 경우 필터링 추가", regionIds);
            }

            logger.debug("지역 필터링 최종 Predicate: {}", predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }



    public static Specification<PostEntity> withAgeGroup(List<AgeGroup> ageGroups){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            logger.debug("연령대 필터링 시작: ageGroups = {}", ageGroups);

            if (ageGroups != null && !ageGroups.isEmpty()){
                // UserEntity의 ageGroup과 비교하여 필터링
                Join<PostEntity, UserEntity> userJoin = root.join("user");
                predicates.add(userJoin.get("ageGroup").in(ageGroups));
                logger.debug("ageGroup in {} 필터링 추가", ageGroups);

            }
            logger.debug("연령대 필터링 최종 Predicate: {}", predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PostEntity> excludeBlockedAndReported(
            List<Long> blockedUserIds,  // 내가 차단/신고한 유저 ID
            List<Long> blockerUserIds,  // 나를 차단/신고한 유저 ID
            List<Long> reportedPostIds  // 내가 신고한 게시물 ID
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
                predicates.add(cb.not(root.get("user").get("userId").in(blockedUserIds)));
            }

            if (blockerUserIds != null && !blockerUserIds.isEmpty()) {
                predicates.add(cb.not(root.get("user").get("userId").in(blockerUserIds)));
            }

            if (reportedPostIds != null && !reportedPostIds.isEmpty()) {
                predicates.add(cb.not(root.get("postId").in(reportedPostIds)));
            }

            if (predicates.isEmpty()) {
                return null;  // 조건 없음, 필터 안함
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<PostEntity> withCursor(Cursor cursor, String sortBy) {
        if (cursor == null) {
            return null;
        }

        if ("zzimCount".equalsIgnoreCase(sortBy)) {
            // 복합 정렬: zzimCount DESC, createdAt DESC
            // 커서 이후 데이터 조건:
            // zzimCount < cursor.zzimCount OR (zzimCount = cursor.zzimCount AND createdAt < cursor.createdAt)
            return (root, query, cb) -> {
                Predicate zzimLess = cb.lessThan(root.get("zzimCount"), cursor.zzimCount());
                Predicate zzimEqualCreatedAtLess = cb.and(
                        cb.equal(root.get("zzimCount"), cursor.zzimCount()),
                        cb.lessThan(root.get("createdAt"), cursor.createdAt())
                );
                return cb.or(zzimLess, zzimEqualCreatedAtLess);
            };
        } else {
            // 단일 정렬: createdAt DESC
            // 커서 이후 데이터 조건: createdAt < cursor.createdAt
            return (root, query, cb) ->
                    cb.lessThan(root.get("createdAt"), cursor.createdAt());
        }
    }
    public static Specification<PostEntity> buildFilterSpec(
            List<Long> categoryIds,
            List<Long> regionIds,
            List<AgeGroup> ageGroups,
            boolean isLocalReview,
            String sortBy,
            Cursor cursor,
            List<Long> blockedUserIds,
            List<Long> blockerUserIds,
            List<Long> reportedPostIds) {

        Specification<PostEntity> localReviewSpec = withLocalReview(categoryIds);
        Specification<PostEntity> regionSpec = withRegionIds(regionIds);

        Specification<PostEntity> ageGroupSpec = (ageGroups != null && !ageGroups.isEmpty())
                ? PostSpecification.withAgeGroup(ageGroups)
                : null;

        boolean onlyLocalCategory = categoryIds != null && categoryIds.size() == 1 && categoryIds.contains(2L);
        Specification<PostEntity> categorySpec = !onlyLocalCategory
                ? withCategoryIds(categoryIds)
                : null;

        Specification<PostEntity> exclusionSpec = excludeBlockedAndReported(blockedUserIds, blockerUserIds, reportedPostIds);
        Specification<PostEntity> cursorSpec = PostSpecification.withCursor(cursor, sortBy);
        Specification<PostEntity> baseSpec = Specification.where(localReviewSpec)
                .and(regionSpec)
                .and(ageGroupSpec != null ? ageGroupSpec : Specification.where(null))
                .and(categorySpec != null ? categorySpec : Specification.where(null))
                .and(exclusionSpec != null ? exclusionSpec : Specification.where(null))
                .and(cursorSpec != null ? cursorSpec : Specification.where(null));

        return (root, query, cb) -> {
            Predicate basePredicate = baseSpec.toPredicate(root, query, cb);

            if ("zzimCount".equalsIgnoreCase(sortBy) && cursor != null) {
                // cursorZzimCount, cursorCreatedAt 둘 다 있어야 함
                Long cursorZzimCount = cursor.zzimCount();
                LocalDateTime cursorCreatedAt = cursor.createdAt();

                // 저장순 기준 + 저장수가 같은 경우 최신순으로 페이징 처리
                Predicate cursorPredicate = cb.or(
                        cb.lessThan(root.get("zzimCount"), cursorZzimCount),
                        cb.and(
                                cb.equal(root.get("zzimCount"), cursorZzimCount),
                                cb.lessThan(root.get("createdAt"), cursorCreatedAt)
                        )
                );

                return cb.and(basePredicate, cursorPredicate);
            }

            if (cursor != null && cursor.createdAt() != null) {
                // 최신순 정렬일 경우 createdAt 기준 페이징
                Predicate cursorPredicate = cb.lessThan(root.get("createdAt"), cursor.createdAt());
                return cb.and(basePredicate, cursorPredicate);
            }

            return basePredicate;
        };
    }

//    public static Specification<PostEntity> buildFilterSpec(
//            List<Long> categoryIds,
//            List<Long> regionIds,
//            List<AgeGroup> ageGroups,
//            boolean isLocalReview,
//            String sortBy,
//            Cursor cursor,
//            List<Long> blockedUserIds,
//            List<Long> blockerUserIds,
//            List<Long> reportedPostIds) {
//
//        Specification<PostEntity> localReviewSpec = withLocalReview(categoryIds);
//        Specification<PostEntity> regionSpec = withRegionIds(regionIds);
//
//        Specification<PostEntity> ageGroupSpec = null;
//        if (ageGroups != null && !ageGroups.isEmpty()) {
//            ageGroupSpec = PostSpecification.withAgeGroup(ageGroups);
//        }
//
//        boolean onlyLocalCategory = categoryIds != null && categoryIds.size() == 1 && categoryIds.contains(2L);
//        Specification<PostEntity> categorySpec = null;
//        if (!onlyLocalCategory) {
//            categorySpec = withCategoryIds(categoryIds);
//        }
//
//        Specification<PostEntity> exclusionSpec = excludeBlockedAndReported(blockedUserIds, blockerUserIds, reportedPostIds);
//
//        Specification<PostEntity> spec = Specification.where(localReviewSpec)
//                .and(regionSpec)
//                .and(ageGroupSpec != null ? ageGroupSpec : Specification.where(null))
//                .and(categorySpec != null ? categorySpec : Specification.where(null))
//                .and(exclusionSpec != null ? exclusionSpec : Specification.where(null));
//
//        return (root, query, cb) -> {
//            Predicate predicate = spec.toPredicate(root, query, cb);
//
//            // 정렬 조건: 저장순 or 최신순
//            if ("zzimCount".equalsIgnoreCase(sortBy)) {
//                // LEFT JOIN으로 zzimCount 계산
//                Join<PostEntity, ZzimPostEntity> zzimPostJoin = root.join("zzims", JoinType.LEFT);
//                query.groupBy(root.get("postId"));
//
//                Expression<Long> zzimCountExpr = cb.count(zzimPostJoin.get("post"));
//
//                // 커서 조건 (zzimCount ↓, createdAt ↓)
//                if (cursorZzimCount != null && cursorCreatedAt != null) {
//                    Predicate zzimCountPredicate = cb.lessThan(zzimCountExpr, cursorZzimCount);
//                    Predicate zzimCountEqual = cb.equal(zzimCountExpr, cursorZzimCount);
//                    Predicate createdAtPredicate = cb.lessThan(root.get("createdAt"), cursorCreatedAt);
//
//                    Predicate combinedCursor = cb.or(
//                            zzimCountPredicate,
//                            cb.and(zzimCountEqual, createdAtPredicate)
//                    );
//
//                    predicate = predicate != null ? cb.and(predicate, combinedCursor) : combinedCursor;
//                }
//
//                // 정렬: 저장수 DESC, 최신순 DESC
//                query.orderBy(cb.desc(zzimCountExpr), cb.desc(root.get("createdAt")));
//
//            } else {
//                // 최신순 커서 조건
//                if (cursorCreatedAt != null) {
//                    Predicate createdAtPredicate = cb.lessThan(root.get("createdAt"), cursorCreatedAt);
//                    predicate = predicate != null ? cb.and(predicate, createdAtPredicate) : createdAtPredicate;
//                }
//
//                // 정렬: 최신순
//                query.orderBy(cb.desc(root.get("createdAt")));
//            }
//
//            return predicate;
//        };
//    }

//    public static Specification<PostEntity> buildFilterSpec(
//            List<Long> categoryIds,
//            List<Long> regionIds,
//            List<AgeGroup> ageGroups,
//            boolean isLocalReview,
//            String sortBy,
//            Long cursor,
//            List<Long> blockedUserIds,
//            List<Long> blockerUserIds,
//            List<Long> reportedPostIds) {
//
//        Specification<PostEntity> localReviewSpec = withLocalReview(categoryIds);
//        Specification<PostEntity> regionSpec = withRegionIds(regionIds);
//
//        Specification<PostEntity> ageGroupSpec = null;
//        if (ageGroups != null && !ageGroups.isEmpty()) {
//            ageGroupSpec = PostSpecification.withAgeGroup(ageGroups);
//        }
//
//        boolean onlyLocalCategory = categoryIds != null && categoryIds.size() == 1 && categoryIds.contains(2L);
//        Specification<PostEntity> categorySpec = null;
//        if (!onlyLocalCategory) {
//            categorySpec = withCategoryIds(categoryIds);
//        }
//
//        Specification<PostEntity> exclusionSpec = excludeBlockedAndReported(blockedUserIds, blockerUserIds, reportedPostIds);
//
//        Specification<PostEntity> spec = Specification.where(localReviewSpec)
//                .and(regionSpec)
//                .and(ageGroupSpec != null ? ageGroupSpec : Specification.where(null))
//                .and(categorySpec != null ? categorySpec : Specification.where(null))
//                .and(exclusionSpec != null ? exclusionSpec : Specification.where(null));
//
//        return (root, query, cb) -> {
//            Predicate predicate = spec.toPredicate(root, query, cb);
//
//            if (cursor != null) {
//                Path<Long> postIdPath = root.get("postId");
//                Predicate cursorPredicate = cb.lessThan(postIdPath, cursor);
//                if (predicate != null) {
//                    predicate = cb.and(predicate, cursorPredicate);
//                } else {
//                    predicate = cursorPredicate;
//                }
//            }
//
//            if ("zzimCount".equalsIgnoreCase(sortBy)) {
//                Join<PostEntity, ZzimPostEntity> zzimPostJoin = root.join("zzims", JoinType.LEFT);
//                Expression<Long> zzimCount = cb.count(zzimPostJoin.get("post"));
//
//                query.groupBy(root.get("postId"));
//                query.orderBy(cb.desc(zzimCount), cb.desc(root.get("createdAt")));
//            } else {
//                query.orderBy(cb.desc(root.get("postId")));
//            }
//
//            return predicate;
//        };
//    }
//
 }






