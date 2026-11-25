package com.mysite.sbb;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;

// 개발 DB(~/local) 대신 이 테스트만의 메모리 DB 사용, 학습 데이터·모델이 없는 상태에서 시작
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:sbb-safety;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class SafetyIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	@DisplayName("학습 전 추론은 500 대신 안내 화면")
	void predictBeforeTrainingShowsNotice() throws Exception {
		this.mockMvc.perform(post("/predict/run")
				.param("equipmentName", "CVD-01")
				.param("temperature", "72")
				.param("pressure", "2.1")
				.param("vibration", "0.3")
				.param("gasFlow", "120"))
			.andExpect(status().isConflict())
			.andExpect(content().string(Matchers.containsString("학습된 모델이 없습니다")))
			.andExpect(content().string(Matchers.containsString("/model/info")));
	}

	@Test
	@DisplayName("학습 전 검증은 500 대신 안내 화면")
	void validationBeforeTrainingShowsNotice() throws Exception {
		this.mockMvc.perform(get("/validation/run"))
			.andExpect(status().isConflict())
			.andExpect(content().string(Matchers.containsString("학습된 모델이 없습니다")));
	}

	@Test
	@DisplayName("정상 데이터 없이 학습하면 안내 화면")
	void trainWithoutDataShowsNotice() throws Exception {
		this.mockMvc.perform(get("/model/train"))
			.andExpect(status().isConflict())
			.andExpect(content().string(Matchers.containsString("정상(NORMAL) 학습 데이터가 없습니다")))
			.andExpect(content().string(Matchers.containsString("/sensor/list")));
	}

	@Test
	@DisplayName("질문 삭제는 POST로만 (주소만 열어서는 안 지워짐)")
	void questionDeleteNeedsPost() throws Exception {
		Question q = new Question();
		q.setSubject("삭제 테스트");
		q.setContent("내용");
		q.setCreateDate(LocalDateTime.now());
		Integer id = this.questionRepository.save(q).getId();

		// 주소만 열어서는 지워지지 않음
		this.mockMvc.perform(get("/question/delete/" + id))
			.andExpect(status().isMethodNotAllowed());
		assertTrue(this.questionRepository.existsById(id));

		this.mockMvc.perform(post("/question/delete/" + id))
			.andExpect(redirectedUrl("/question/list"));
		assertFalse(this.questionRepository.existsById(id));
	}
}
