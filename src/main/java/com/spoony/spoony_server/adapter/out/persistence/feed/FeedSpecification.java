package com.spoony.spoony_server.adapter.out.persistence.feed;


import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedEntity;
import com.spoony.spoony_server.adapter.out.persistence.feed.db.FeedRepository;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class FeedSpecification {

    public static Specification<FeedEntity> hasUserId(Long userId){
        return (root,query,cb) -> cb.equal(root.get("user").get("id"),userId);
    }


    //다중 카테고리 필터링

    public static Specification<FeedEntity> withCategories(List<String> categoryNames) {
        return (root, query, cb) -> {
            if (categoryNames == null || categoryNames.isEmpty()) {
                return cb.conjunction();
            }
            Join<FeedEntity, PostEntity> postJoin = root.join("post");
            return postJoin.get("category").in(categoryNames); //WHERE post.category IN ('여행', '맛집', '산책')
        };
    }

    //지역 검색
    public static Specification<FeedEntity> hasLocation(String locationQuery){
        return (root, query, cb) ->{


            if (locationQuery == null || locationQuery.isBlank()) {
                return cb.conjunction();
            }
            Join<FeedEntity, PostEntity> postJoin = root.join("post");
            return cb.like(postJoin.get("location"), "%" + locationQuery + "%"); //WHERE post.location LIKE '%서울%'
        };
        }


    //조립 담당 매서드
    public static Specification<FeedEntity> buildFeedSpec(Long userId, List<String> categories, String locationQuery){
        Specification<FeedEntity> spec = Specification.where(FeedSpecification.hasUserId(userId));

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and(FeedSpecification.withCategories(categories));
        }

        if (locationQuery != null && !locationQuery.isBlank()) {
            spec = spec.and(FeedSpecification.hasLocation(locationQuery));
        }

        return spec;
    }
    }



