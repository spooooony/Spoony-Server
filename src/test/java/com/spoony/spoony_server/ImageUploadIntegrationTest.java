package com.spoony.spoony_server;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoony.spoony_server.adapter.dto.file.request.PresignedUrlRequestDTO;
import com.spoony.spoony_server.application.port.out.file.S3PresignedUrlPort;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ImageUploadIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;

	// ✅ 오직 ImageUploadController가 의존하는 포트만 MockBean으로 설정합니다.
	// PresignedUrlCreateUseCase는 PresignedUrlService에 의해 구현되고,
	// 이 Service는 S3PresignedUrlPort를 의존합니다.
	@MockBean
	private S3PresignedUrlPort s3PresignedUrlPort;

	@BeforeEach
	void setup() throws Exception {
		// ✅ S3PresignedUrlPort의 동작만 Stubbing합니다.
		// 테스트 URL을 반환하도록 설정
		URL fakeUrl = new URL("https://mock-s3-bucket.s3.amazonaws.com/test/image-key");

		// given/when 대신 doReturn().when() 방식을 사용하여 AOP 프록시 문제를 피합니다.
		Mockito.doReturn(fakeUrl)
			.when(s3PresignedUrlPort)
			.generatePresignedPutUrl(anyString(), anyString());

		// 유저, 게시물, 장소 관련 Mocking 코드는 모두 제거합니다.
	}

	@Test
	@DisplayName("Presigned URL 발급 성공")
	void generatePresignedUrl_success() throws Exception {
		// 1️⃣ Presigned URL 발급 요청 DTO 준비
		PresignedUrlRequestDTO request = new PresignedUrlRequestDTO("my-photo.png", "image/png");

		// 2️⃣ API 호출 및 결과 검증
		var result = mockMvc.perform(post("/api/v1/image/presigned")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.method").value("PUT"))
			.andReturn();

		// 3️⃣ 응답 본문 검증
		String content = result.getResponse().getContentAsString();
		assertThat(content).contains("mock-s3-bucket.s3.amazonaws.com");

		// 4️⃣ Mocking이 제대로 호출되었는지 검증 (선택적)
		verify(s3PresignedUrlPort, times(1)).generatePresignedPutUrl(anyString(), eq("image/png"));
	}
}
