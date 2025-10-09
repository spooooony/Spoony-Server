package com.spoony.spoony_server;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoony.spoony_server.adapter.dto.post.request.PostCreateRequestDTO;
import com.spoony.spoony_server.adapter.dto.post.request.PostUpdateRequestDTO;
import com.spoony.spoony_server.adapter.out.persistence.feed.event.PostCreatedEventListener;
import com.spoony.spoony_server.application.port.in.file.PresignedUrlCreateUseCase;
import com.spoony.spoony_server.application.port.out.feed.FeedPort;
import com.spoony.spoony_server.application.port.out.file.S3DeletePort;
import com.spoony.spoony_server.application.port.out.place.PlacePort;
import com.spoony.spoony_server.application.port.out.post.CategoryPort;
import com.spoony.spoony_server.application.port.out.post.PhotoPort;
import com.spoony.spoony_server.application.port.out.post.PostCategoryPort;
import com.spoony.spoony_server.application.port.out.post.PostDeletePort;
import com.spoony.spoony_server.application.port.out.post.PostPort;
import com.spoony.spoony_server.application.port.out.report.ReportPort;
import com.spoony.spoony_server.application.port.out.user.BlockPort;
import com.spoony.spoony_server.application.port.out.user.RegionPort;
import com.spoony.spoony_server.application.port.out.user.UserPort;
import com.spoony.spoony_server.application.port.out.zzim.ZzimPostPort;
import com.spoony.spoony_server.domain.place.Place;
import com.spoony.spoony_server.domain.post.Category;
import com.spoony.spoony_server.domain.post.CategoryType;
import com.spoony.spoony_server.domain.post.Post;
import com.spoony.spoony_server.domain.post.PostCategory;
import com.spoony.spoony_server.domain.user.AgeGroup;
import com.spoony.spoony_server.domain.user.Platform;
import com.spoony.spoony_server.domain.user.Region;
import com.spoony.spoony_server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostImageFlowIntegrationTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;

	@MockBean private UserPort userPort;
	@MockBean private PostPort postPort;
	@MockBean private PlacePort placePort;
	@MockBean private RegionPort regionPort;
	@MockBean private CategoryPort categoryPort;
	@SpyBean private ZzimPostPort zzimPostPort;
	@MockBean private FeedPort feedPort;
	@MockBean private PostCategoryPort postCategoryPort;
	@SpyBean private BlockPort blockPort;
	@MockBean private ReportPort reportPort;
	@MockBean private PostCreatedEventListener postCreatedEventListener;
	@MockBean private PostDeletePort postDeletePort;
	@MockBean private PhotoPort photoPort;
	@MockBean private PresignedUrlCreateUseCase presignedUrlCreateUseCase;
	@MockBean private S3DeletePort s3DeletePort;

	@MockBean private ApplicationEventPublisher eventPublisher;

	private User mockUser;
	private Place mockPlace;
	private Post mockPost;

	@BeforeEach
	void setup() throws Exception {
		Region testRegion = new Region(1L, "강남구");

		//@UserId Argument Resolver를 위한 Security Context 직접 설정
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			1L,
			null,
			List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);


		// 1.
		mockUser = new User(
			1L, Platform.KAKAO, "test-platform-id", 1L, 1L,
			"테스트유저", testRegion, "테스트 유저입니다",
			LocalDate.of(2000, 1, 1), AgeGroup.AGE_20S,
			LocalDateTime.now(), LocalDateTime.now()
		);

		Mockito.doReturn(mockUser)
			.when(userPort).findUserById(Mockito.any(Long.class));


		//  2. 장소 Mock
		mockPlace = new Place(
			"스푸니식당", "서울시 강남구", "서울시 강남구 테헤란로",
			37.123, 127.456, testRegion
		);
		Mockito.doReturn(mockPlace).when(placePort).findByPlaceNameAndCoordinates(any(), anyDouble(), anyDouble());

		// 3. 게시글 Mock
		mockPost = new Post(
			mockUser, mockPlace, "테스트 게시글입니다.",
			4.0, "조금 비쌈", 0L, LocalDateTime.now(), LocalDateTime.now()
		);


		Mockito.doReturn(1L).when(postPort).savePost(any(Post.class));
		Mockito.doReturn(mockPost).when(postPort).findPostById(1L);

		Mockito.doNothing().when(postPort).savePostCategory(any());
		Mockito.doNothing().when(postPort).saveMenu(any());
		Mockito.doNothing().when(postPort).savePhoto(any());
		Mockito.doNothing().when(postPort).updatePost(anyLong(), any(), anyDouble(), any());
		Mockito.doNothing().when(postPort).deleteAllPhotosByPhotoUrl(any());

		// 4. 기타 Mock
		Category foodCategory = new Category(
			1L, CategoryType.FOOD, "음식", null, null, null, null, null);
		Mockito.doReturn(foodCategory).when(categoryPort).findCategoryById(anyLong());
		Mockito.doReturn(testRegion).when(regionPort).findByAddress(any());
		Mockito.doNothing().when(zzimPostPort).saveZzimPost(any(), any());
		Mockito.doReturn(new PostCategory(mockPost, foodCategory)).when(postCategoryPort).findPostCategoryByPostId(anyLong());

		// 5. PhotoPort/S3DeletePort Mock
		List<String> mockDeleteUrls = List.of("https://fake-s3-url.com/posts/2025/10/old-image-key");
		Mockito.doReturn(mockDeleteUrls)
			.when(photoPort).getPhotoUrls(anyLong());
		Mockito.doNothing().when(s3DeletePort).deleteImagesFromS3(any());
	}


	@Test
	@DisplayName("Presigned URL 기반 게시물 등록 성공")
	void createPost_withPresignedUrl_success() throws Exception {
		List<String> photoUrls = List.of("https://fake-s3-url.com/posts/2025/10/fake-key");

		PostCreateRequestDTO request = new PostCreateRequestDTO(
			"테스트 게시글입니다.", 4.0, "조금 비쌈", "스푸니식당", "서울시 강남구",
			"서울시 강남구 테헤란로", 37.123, 127.456, 1L, List.of("짜장면", "탕수육"), photoUrls
		);

		mockMvc.perform(post("/api/v1/post")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		// ✅ 검증: PostService 로직에 따라 필수 메서드 호출 검증
		Mockito.verify(postPort).savePost(any(Post.class));
		Mockito.verify(postPort, Mockito.times(photoUrls.size())).savePhoto(any());
	}

	@Test
	@DisplayName("Presigned URL 기반 게시물 수정 성공")
	void updatePost_withPresignedUrl_success() throws Exception {
		List<String> newUrls = List.of("https://fake-s3-url.com/posts/2025/10/new-image-key");
		List<String> deleteUrls = List.of("https://fake-s3-url.com/posts/2025/10/old-image-key");

		PostUpdateRequestDTO request = new PostUpdateRequestDTO(
			1L, "수정된 설명입니다.", 4.5, "조금 덜 짬", 1L, List.of("짬뽕", "탕수육"), newUrls, deleteUrls
		);

		var result = mockMvc.perform(patch("/api/v1/post")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andReturn();

		String body = result.getResponse().getContentAsString();
		assertThat(body).contains("\"success\":true");

		Mockito.verify(s3DeletePort).deleteImagesFromS3(deleteUrls);
		Mockito.verify(postPort).updatePost(anyLong(), any(), anyDouble(), any());
		Mockito.verify(postPort).deleteAllPhotosByPhotoUrl(deleteUrls);
		Mockito.verify(postPort, Mockito.times(newUrls.size())).savePhoto(any());
	}
}