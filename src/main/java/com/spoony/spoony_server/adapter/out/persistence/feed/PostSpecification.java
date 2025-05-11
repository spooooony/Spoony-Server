package com.spoony.spoony_server.adapter.out.persistence.feed;


import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import com.spoony.spoony_server.domain.user.AgeGroup;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;


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
                Predicate nullRegionPredicate = cb.isNull(placeJoin.get("region")); // region_id가 null인 경우

                // 두 조건을 OR로 결합하여 region_id가 null인 경우도 필터링
                predicates.add(cb.or(regionPredicate, nullRegionPredicate));
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


    public static Specification<PostEntity> buildFilterSpec(
            List<Long> categoryIds,
            List<Long> regionIds,
            List<AgeGroup> ageGroups,
            boolean isLocalReview,
            String sortBy) {

        // 로컬리뷰 필터
        Specification<PostEntity> localReviewSpec = withLocalReview(categoryIds);

        // 지역 필터
        Specification<PostEntity> regionSpec = withRegionIds(regionIds);

        // 연령대 필터
        Specification<PostEntity> ageGroupSpec = null;
        if (ageGroups != null && !ageGroups.isEmpty()) {
            ageGroupSpec = PostSpecification.withAgeGroup(ageGroups);
        }

        // 카테고리 필터
        boolean onlyLocalCategory = categoryIds.size() == 1 && categoryIds.contains(2L);
        Specification<PostEntity> categorySpec = null;
        if (!onlyLocalCategory) {
            categorySpec = withCategoryIds(categoryIds);
        }

        // 필터 결합
        Specification<PostEntity> spec = Specification.where(localReviewSpec)
                .and(regionSpec)
                .and(ageGroupSpec != null ? ageGroupSpec : Specification.where(null))
                .and(categorySpec != null ? categorySpec : Specification.where(null));

        // 정렬 처리
        return (root, query, cb) -> {
            // 기본 필터링된 조건
            Predicate predicate = spec.toPredicate(root, query, cb);

            // 정렬 조건 추가
            if ("zzimCount".equalsIgnoreCase(sortBy)) {
                // 찜한 개수 기준으로 정렬 (JOIN 후 카운트로 정렬)
                Join<PostEntity, ZzimPostEntity> zzimPostJoin = root.join("zzims", JoinType.LEFT);
                Expression<Long> zzimCount = cb.count(zzimPostJoin.get("post"));

                query.groupBy(root.get("postId"));
                query.orderBy(cb.desc(zzimCount));  // 찜한 개수가 많은 순으로 정렬

            } else {
                // 최신순으로 정렬 (createdAt 기준)
                query.orderBy(cb.desc(root.get("createdAt")));
            }

            return predicate;  // 필터링된 결과에 정렬 조건을 추가한 쿼리 반환
        };
    }



//    public static Specification<PostEntity> buildFilterSpec(List<Long> categoryIds, List<Long> regionIds,List<AgeGroup> ageGroups, boolean isLocalReview) {
//        Specification<PostEntity> spec = Specification.where(null);
//
//
//        // 로컬리뷰 필터
//        spec = spec.and(withLocalReview(categoryIds));
//
//        // 지역 필터
//        spec = spec.and(withRegionIds(regionIds));
//
//        //연령대 필터
//        if (ageGroups != null && !ageGroups.isEmpty()) {
//            spec = spec.and(PostSpecification.withAgeGroup(ageGroups));
//        }
//
//
//        // ✅ category_id = 2 단독일 경우, 카테고리 필터는 적용하지 않음
//        boolean onlyLocalCategory = categoryIds.size() == 1 && categoryIds.contains(2L);
//        if (!onlyLocalCategory) {
//            spec = spec.and(withCategoryIds(categoryIds));
//        }
//
//        //return spec;
//
//    }




}
