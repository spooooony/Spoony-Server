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
            Join<ZzimPostEntity, PostEntity> postJoin = root.join("post");

            Predicate notDeleted = cb.isFalse(postJoin.get("isDeleted"));

            if (categoryId != null && categoryId == 1L) {
                Predicate userPredicate = cb.equal(root.get("user").get("userId"), userId);
                return cb.and(userPredicate, notDeleted);
            } else {
                Join<PostEntity, PostCategoryEntity> pcJoin = postJoin.join("postCategories");

                Predicate userPredicate = cb.equal(root.get("user").get("userId"), userId);
                Predicate categoryPredicate = cb.equal(pcJoin.get("category").get("categoryId"), categoryId);

                return cb.and(userPredicate, categoryPredicate, notDeleted);
            }
        };
    }

    public static Specification<ZzimPostEntity> withUserId(Long userId) {
        return (root, query, cb) -> {
            Join<ZzimPostEntity, PostEntity> postJoin = root.join("post");

            Predicate userPredicate = cb.equal(root.get("user").get("userId"), userId);
            Predicate notDeleted = cb.isFalse(postJoin.get("isDeleted"));

            return cb.and(userPredicate, notDeleted);
        };
    }
}
