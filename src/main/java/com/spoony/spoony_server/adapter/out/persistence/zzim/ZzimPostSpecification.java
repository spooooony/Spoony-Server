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
                return cb.equal(root.get("user").get("userId"), userId);
            } else {
                Join<ZzimPostEntity, PostEntity> postJoin = root.join("post");
                Join<PostEntity, PostCategoryEntity> pcJoin = postJoin.join("postCategories");

                Predicate userPredicate = cb.equal(root.get("user").get("userId"), userId);
                Predicate categoryPredicate = cb.equal(pcJoin.get("category").get("categoryId"), categoryId);

                return cb.and(userPredicate, categoryPredicate);
            }
        };
    }

    public static Specification<ZzimPostEntity> withUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("userId"), userId);
    }
}
