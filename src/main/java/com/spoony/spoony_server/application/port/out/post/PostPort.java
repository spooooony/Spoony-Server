package com.spoony.spoony_server.application.port.out.post;

import com.spoony.spoony_server.adapter.dto.Cursor;
import com.spoony.spoony_server.domain.post.Menu;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.post.PostCategory;
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;

public interface PostPort {
    List<Post> findPostsByUserId(Long userId, int page, int size);
    int countPostsByUserId(Long userId); // 페이지네이션 위한 총 개수
    List<Post> findPostsByUserId (Long userId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Post findPostById(Long postId);
    List<Photo> findPhotoById(Long postId);
    List<Menu> findMenuById(Long postId);
    Long savePost(Post post);
    void savePostCategory(PostCategory postCategory);
    void saveMenu(Menu menu);
    void savePhoto(Photo photo);
    void saveScoopPost(User user, Post post);
    void deleteById(Long postId);
    void updatePost(Long postId, String description, Double value, String cons);
    void deleteAllPostCategoryByPostId(Long postId);
    void deleteAllMenusByPostId(Long postId);
    void deleteAllPhotosByPhotoUrl(List<String> deletePhotoUrlList);
    Long countPostsByUserIdExcludingReported(Long targetUserId,List <Long> reportedPostIds);
    List<Post> findByPostDescriptionContaining(String query);
    List<Long> getReportedPostIds(Long userId);

    List<Post> findFilteredPosts(List<Long> categoryIds,
                                 List<Long> regionIds,
                                 List<AgeGroup>ageGroups,String sortBy,
                                 boolean isLocalReview,
                                 Cursor cursor,
                                 int size,
                                 List<Long> blockedUserIds,
                                 List<Long> reportedUserIds,
                                 List<Long> reportedPostIds);
    List<Post> findAllPosts(int page, int size);
    int countAllPosts();
    List<Post> findReportedPosts(int page, int size);
    int countReportedPosts();
}
