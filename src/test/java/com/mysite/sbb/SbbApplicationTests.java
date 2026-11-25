package com.mysite.sbb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;

@SpringBootTest
@ActiveProfiles("test")
class SbbApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	@DisplayName("질문을 저장하면 같은 내용으로 다시 읽힌다")
	void saveAndFindQuestion() {
		Question q = new Question();
		q.setSubject("sbb가 무엇인가요?");
		q.setContent("sbb에 대해서 알고 싶습니다.");
		q.setCreateDate(LocalDateTime.now());
		Integer id = this.questionRepository.save(q).getId();

		Question found = this.questionRepository.findById(id).orElseThrow();
		assertEquals("sbb가 무엇인가요?", found.getSubject());
		assertEquals("sbb에 대해서 알고 싶습니다.", found.getContent());
		assertTrue(this.questionRepository.count() >= 1);
	}
}
