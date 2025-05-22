package com.spoony.spoony_server.adapter.out.persistence.zzim;

import com.spoony.spoony_server.adapter.out.persistence.post.db.PostCategoryEntity;
import com.spoony.spoony_server.adapter.out.persistence.post.db.PostEntity;
import com.spoony.spoony_server.adapter.out.persistence.zzim.db.ZzimPostEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ZzimPostSpecification {

    public static Specification<ZzimPostEntity> withUserIdAndCategoryId(Long userId, Long categoryId){
        return (root, query, cb) -> {
            if (categoryId != null && categoryId == 1L) {
                // categoryId=1이면 전체조회 → userId만 필터링
                return cb.equal(root.get("user").get("userId"), userId);
            } else {
                // categoryId != 1 → userId와 categoryId 모두 필터링
                Join<ZzimPostEntity, PostEntity> postJoin = root.join("post");
                Join<PostEntity, PostCategoryEntity> pcJoin = postJoin.join("postCategories");

                Predicate userPredicate = cb.equal(root.get("user").get("userId"), userId);
                Predicate categoryPredicate = cb.equal(pcJoin.get("category").get("categoryId"), categoryId);

                return cb.and(userPredicate, categoryPredicate);
            }
        };
    }

    //커서 페이징
    public static Specification<ZzimPostEntity> withUserIdCategoryIdAndCursor(Long userId, Long categoryId, Long cursor){
        Specification<ZzimPostEntity> baseSpec = withUserIdAndCategoryId(userId,categoryId);

        if (cursor == null) {
            return baseSpec;
        }

        Specification<ZzimPostEntity> cursorSpec = (root, query, cb) ->
                cb.lessThan(root.get("zzimId"), cursor);

        return baseSpec.and(cursorSpec);

    }

}
