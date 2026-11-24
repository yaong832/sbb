package com.mysite.sbb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.mysite.sbb.model.ModelInfo;
import com.mysite.sbb.model.ModelService;
import com.mysite.sbb.predict.PredictionResult;
import com.mysite.sbb.predict.PredictionService;
import com.mysite.sbb.sensor.SensorDataRepository;
import com.mysite.sbb.sensor.SensorDataService;
import com.mysite.sbb.validation.ValidationResult;
import com.mysite.sbb.validation.ValidationService;

// 샘플 생성 → 학습 → 추론 → 검증 흐름
@SpringBootTest
@ActiveProfiles("test")
class PipelineIntegrationTest {

	@Autowired
	private SensorDataRepository sensorDataRepository;
	@Autowired
	private SensorDataService sensorDataService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private PredictionService predictionService;
	@Autowired
	private ValidationService validationService;

	@BeforeEach
	void setUp() {
		this.sensorDataRepository.deleteAll();
		this.sensorDataService.generateSampleData();
	}

	@Test
	@DisplayName("학습한 정상 평균은 샘플 정상 데이터 범위 안에 있다")
	void trainUsesNormalDataOnly() {
		ModelInfo model = this.modelService.train();

		// 샘플 정상 데이터: 온도 65~80, 압력 1.7~2.5, 진동 0.1~0.5, 가스 100~135 (정상 20건)
		assertEquals(20, model.getTrainDataCount());
		assertTrue(model.getAvgTemperature() >= 65 && model.getAvgTemperature() <= 80);
		assertTrue(model.getAvgPressure() >= 1.7 && model.getAvgPressure() <= 2.5);
		assertTrue(model.getAvgVibration() >= 0.1 && model.getAvgVibration() <= 0.5);
		assertTrue(model.getAvgGasFlow() >= 100 && model.getAvgGasFlow() <= 135);
	}

	@Test
	@DisplayName("평균값을 넣으면 정상, 이상 샘플 범위 값을 넣으면 이상")
	void predictNormalAndAbnormal() {
		ModelInfo model = this.modelService.train();

		PredictionResult normal = this.predictionService.predict("Etcher-01", model.getAvgTemperature(),
				model.getAvgPressure(), model.getAvgVibration(), model.getAvgGasFlow());
		assertEquals("NORMAL", normal.getResult());
		assertEquals(0, normal.getRiskScore());

		PredictionResult abnormal = this.predictionService.predict("Etcher-01", 100.0, 4.5, 2.0, 200.0);
		assertEquals("ABNORMAL", abnormal.getResult());
		assertEquals(100, abnormal.getRiskScore());
	}

	@Test
	@DisplayName("검증은 저장된 데이터 전체(정상 20 + 이상 10)를 센다")
	void validationCountsAllData() {
		this.modelService.train();

		ValidationResult result = this.validationService.runValidation();

		assertEquals(30, result.getTotalCount());
		assertEquals(result.getTotalCount(), result.getCorrectCount() + result.getWrongCount());
	}
}
