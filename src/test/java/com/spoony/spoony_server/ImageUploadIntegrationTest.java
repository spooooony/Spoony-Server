package com.spoony.spoony_server;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

	@MockBean
	private S3PresignedUrlPort s3PresignedUrlPort;

	@BeforeEach
	void setup() throws Exception {
		URL fakeUrl = new URL("https://mock-s3-bucket.s3.amazonaws.com/test/image-key");
		Mockito.doReturn(fakeUrl)
			.when(s3PresignedUrlPort)
			.generatePresignedPutUrl(anyString(), anyString());

	}

	@Test
	@DisplayName("Presigned URL 발급 성공")
	void generatePresignedUrl_success() throws Exception {

		PresignedUrlRequestDTO request = new PresignedUrlRequestDTO("my-photo.png", "image/png");


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

		verify(s3PresignedUrlPort, times(1)).generatePresignedPutUrl(anyString(), eq("image/png"));
	}
}
