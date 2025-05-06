package com.spoony.spoony_server.adapter.out.persistence.feed;


import com.spoony.spoony_server.adapter.out.persistence.place.db.PlaceEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.user.db.UserEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostSpecification {

    private static final Logger logger = LoggerFactory.getLogger(PostSpecification.class);

    // 카테고리 필터링 (category_id = 1은 단독 사용, category_id = 2는 3~9와 중복 가능)
    public static Specification<PostEntity> withCategoryIds(List<Long> categoryIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            logger.debug("카테고리 필터링 시작: categoryIds = {}", categoryIds);

            // category_id = 1 (전체) 처리
            if (categoryIds.contains(1L)) {
                if (categoryIds.size() > 1) {
                    // category_id = 1은 단독으로만 사용됨
                    throw new IllegalArgumentException("category_id 1은 단독으로만 사용할 수 있습니다.");
                }
                predicates.add(cb.equal(root.get("categoryId"), 1L)); // categoryId = 1 필터링
                logger.debug("category_id = 1 필터링 추가");
            } else {
                // 로컬리뷰(category_id = 2)와 다른 카테고리들 (category_id=3~9) 처리
                Join<PostEntity, PostCategoryEntity> postCategoryJoin = root.join("postCategories");
                predicates.add(postCategoryJoin.get("category").get("categoryId").in(categoryIds));
                logger.debug("category_id in {} 필터링 추가", categoryIds);
            }

            logger.debug("카테고리 필터링 최종 Predicate: {}", predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
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

    // 카테고리 및 지역 필터 결합
    public static Specification<PostEntity> withCategoryAndRegion(List<Long> categoryIds, List<Long> regionIds) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 카테고리 필터링
            if (categoryIds.contains(1L)) { // '전체' 카테고리일 경우
                // 모든 게시물 반환 (category_id에 필터링 없음)
            } else if (categoryIds.contains(2L)) { // '로컬 수저' 카테고리일 경우
                // 작성자 지역과 게시물의 지역이 동일한지 체크
                Join<PostEntity, PlaceEntity> placeJoin = root.join("place");
                Join<PostEntity, UserEntity> userJoin = root.join("user");
                predicates.add(builder.equal(placeJoin.get("region").get("regionId"), userJoin.get("region").get("regionId")));
            } else {
                // 카테고리 3~9에 해당하는 경우 해당 카테고리만 필터링
                predicates.add(root.get("category").get("categoryId").in(categoryIds));
            }

            // 지역 필터링
            if (regionIds != null && !regionIds.isEmpty()) {
                predicates.add(root.get("place").get("region").get("regionId").in(regionIds));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
