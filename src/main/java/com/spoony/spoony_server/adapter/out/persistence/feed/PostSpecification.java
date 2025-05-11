package com.spoony.spoony_server.adapter.out.persistence.feed;


import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import com.spoony.spoony_server.domain.user.AgeGroup;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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
            List<Predicate> predicates = new ArrayList<>();
            logger.debug("카테고리 필터링 시작: categoryIds = {}", categoryIds);

            Join<PostEntity, PostCategoryEntity> postCategoryJoin = root.join("postCategories");

            // category_id = 1 (전체) 처리
            if (categoryIds.contains(1L)) {
                if (categoryIds.size() > 1) {
                    // category_id = 1은 단독으로만 사용됨
                    throw new IllegalArgumentException("category_id 1은 단독으로만 사용할 수 있습니다.");
                }

                predicates.add(cb.equal(postCategoryJoin.get("category").get("categoryId"), 1L));
                logger.debug("category_id = 1 필터링 추가");
            } else {
                predicates.add(postCategoryJoin.get("category").get("categoryId").in(categoryIds));
                logger.debug("category_id in {} 필터링 추가", categoryIds);
            }

            logger.debug("카테고리 필터링 최종 Predicate: {}", predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
//    public static Specification<PostEntity> withCategoryIds(List<Long> categoryIds) {
//        return (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            logger.debug("카테고리 필터링 시작: categoryIds = {}", categoryIds);
//
//            Join<PostEntity, PostCategoryEntity> postCategoryJoin = root.join("postCategories");
//            // category_id = 1 (전체) 처리
//            if (categoryIds.contains(1L)) {
//                if (categoryIds.size() > 1) {
//                    // category_id = 1은 단독으로만 사용됨
//                    throw new IllegalArgumentException("category_id 1은 단독으로만 사용할 수 있습니다.");
//                }
//                predicates.add(cb.equal(root.get("categoryId"), 1L)); // categoryId = 1 필터링
//                logger.debug("category_id = 1 필터링 추가");
//            } else {
//                // 로컬리뷰(category_id = 2)와 다른 카테고리들 (category_id=3~9) 처리
//                Join<PostEntity, PostCategoryEntity> postCategoryJoin = root.join("postCategories");
//                predicates.add(postCategoryJoin.get("category").get("categoryId").in(categoryIds));
//                logger.debug("category_id in {} 필터링 추가", categoryIds);
//            }
//
//            logger.debug("카테고리 필터링 최종 Predicate: {}", predicates);
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//    }


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

    public static Specification<PostEntity> withCategoryAndRegion(List<Long> categoryIds, List<Long> regionIds) {
        return (root, query, builder) -> {
            Logger logger = LoggerFactory.getLogger(PostSpecification.class);
            List<Predicate> predicates = new ArrayList<>();

            // 카테고리 필터링
            if (categoryIds.contains(1L)) { // '전체' 카테고리일 경우
                logger.info("🟢 [카테고리 필터] 전체 선택됨 (categoryId = 1) → 모든 게시물 반환");
                // 아무 조건도 추가하지 않음
            } else if (categoryIds.contains(2L)) { // '로컬 수저' 카테고리일 경우
                logger.info("📍 [카테고리 필터] 로컬 수저 선택됨 (categoryId = 2) → 작성자 지역과 게시물 지역이 같은 게시물 필터링");

                // place와 user를 조인하여 regionId를 비교
                Join<PostEntity, PlaceEntity> placeJoin = root.join("place");
                Join<PostEntity, UserEntity> userJoin = root.join("user");

                // 로그를 찍어서 regionId를 확인
                logger.info("🔄 place.regionId = {}", placeJoin.get("region").get("regionId"));
                logger.info("👤 user.regionId = {}", userJoin.get("region").get("regionId"));

                // 두 regionId를 비교하는 부분에 equal을 사용
                predicates.add(builder.equal(
                        placeJoin.get("region").get("regionId"),
                        userJoin.get("region").get("regionId")
                ));
            } else {
                logger.info("📦 [카테고리 필터] 특정 카테고리 선택됨 → categoryIds: {}", categoryIds);
                predicates.add(root.get("category").get("categoryId").in(categoryIds));
            }

            // 지역 필터링
            if (regionIds != null && !regionIds.isEmpty()) {
                logger.info("🗺️ [지역 필터] 지역 선택됨 → regionIds: {}", regionIds);
                // 지역 필터링 부분에서 regionId 비교 시 equal 사용
                predicates.add(root.get("place").get("region").get("regionId").in(regionIds));
            } else {
                logger.info("🔓 [지역 필터] 지역 조건 없음 → 전체 지역 대상");
            }

            logger.info("🔎 최종 적용될 필터 개수: {}", predicates.size());

            return builder.and(predicates.toArray(new Predicate[0]));
        };

    }

    public static Specification<PostEntity> buildFilterSpec(List<Long> categoryIds, List<Long> regionIds,List<AgeGroup> ageGroups, boolean isLocalReview) {
        Specification<PostEntity> spec = Specification.where(null);


        // 로컬리뷰 필터
        spec = spec.and(withLocalReview(categoryIds));

        // 지역 필터
        spec = spec.and(withRegionIds(regionIds));

        //연령대 필터
        if (ageGroups != null && !ageGroups.isEmpty()) {
            spec = spec.and(PostSpecification.withAgeGroup(ageGroups));
        }


        // ✅ category_id = 2 단독일 경우, 카테고리 필터는 적용하지 않음
        boolean onlyLocalCategory = categoryIds.size() == 1 && categoryIds.contains(2L);
        if (!onlyLocalCategory) {
            spec = spec.and(withCategoryIds(categoryIds));
        }

        return spec;
    }




}
