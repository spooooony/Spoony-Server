package com.spoony.spoony_server.application.service.feed;

import com.spoony.spoony_server.application.port.command.feed.FeedGetCommand;
import com.spoony.spoony_server.application.port.command.feed.FollowingUserFeedGetCommand;
import com.spoony.spoony_server.application.port.command.user.UserGetCommand;
import com.spoony.spoony_server.application.port.in.feed.FeedGetUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.post.PostCategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.feed.Feed;
import com.spoony.spoony_server.domain.post.Category;
import com.spoony.spoony_server.domain.post.Photo;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.post.PostCategory;
import com.spoony.spoony_server.domain.user.User;
import com.spoony.spoony_server.adapter.dto.post.CategoryColorResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedListResponseDTO;
import com.spoony.spoony_server.adapter.dto.post.FeedResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService implements FeedGetUseCase {

    private final FeedPort feedPort;
    private  final PostPort postPort;
    private final PostCategoryPort postCategoryPort;
    private final ZzimPostPort zzimPostPort;

    public FeedListResponseDTO getFeedListByFollowingUser(FollowingUserFeedGetCommand command) {
//        String locationQuery = command.getLocationQuery();
//        String sortBy = command.getSortBy();

        List<Feed> feedList = feedPort.findFeedListByFollowing(command.getUserId());
        List<FeedResponseDTO> feedResponseList = feedList.stream()
                .map(feed -> {
                    Post post = feed.getPost();
                    User author = post.getUser();
                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    Category category = postCategory.getCategory();

                    List<Photo> photoList = postPort.findPhotoById(post.getPostId());
                    List<String> photoUrlList = photoList.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();
                    return new FeedResponseDTO(
                            author.getUserId(),
                            author.getUserName(),
                            author.getRegion().getRegionName(),
                            post.getPostId(),
                            post.getDescription(),
                            new CategoryColorResponseDTO(
                                    category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()
                            ),
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            photoUrlList,
                            post.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());


        return new FeedListResponseDTO(feedResponseList);
    }

    @Override
    public FeedListResponseDTO getAllPosts() {
        List<Post> postList = postPort.findAll();
        List<FeedResponseDTO> feedResponseList = postList.stream()
                .map(post -> {
                    User author = post.getUser();

                    PostCategory postCategory = postCategoryPort.findPostCategoryByPostId(post.getPostId());
                    Category category = postCategory.getCategory();

                    List<Photo> photoList = postPort.findPhotoById(post.getPostId());
                    List<String> photoUrlList = photoList.stream()
                            .map(Photo::getPhotoUrl)
                            .toList();

                    return new FeedResponseDTO(
                            author.getUserId(),
                            author.getUserName(),
                            author.getRegion().getRegionName(),
                            post.getPostId(),
                            post.getDescription(),
                            new CategoryColorResponseDTO(
                                    category.getCategoryId(),
                                    category.getCategoryName(),
                                    category.getIconUrlColor(),
                                    category.getTextColor(),
                                    category.getBackgroundColor()
                            ),
                            zzimPostPort.countZzimByPostId(post.getPostId()),
                            photoUrlList,
                            post.getCreatedAt()
                    );
                })
                .sorted((dto1, dto2) -> dto2.createdAt().compareTo(dto1.createdAt()))
                .collect(Collectors.toList());

        return new FeedListResponseDTO(feedResponseList);
    }

    @Override
    public FeedListResponseDTO getPostsFromFollowingUsers(UserGetCommand command) {
        return null;
    }


}