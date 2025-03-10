package com.spoony.spoony_server.application.port.out.post;

import com.spoony.spoony_server.domain.post.Menu;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.post.PostCategory;
import com.spoony.spoony_server.domain.user.User;

import java.util.List;
import java.util.Map;

public interface PostPort {
    List<Post> findUserByUserId(Long userId);
    Post findPostWithPhotosAndCategoriesByPostId(Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Post findPostById(Long postId);
    List<Photo> findPhotoById(Long postId);
    List<Menu> findMenuById(Long postId);
    Long savePost(Post post);
    void savePostCategory(PostCategory postCategory);
    void saveMenu(Menu menu);
    void savePhoto(Photo photo);
    void saveScoopPost(User user, Post post);
    Map<Long, PostCategory> findPostCategoriesByPostIds(List<Long> postIds);
}
