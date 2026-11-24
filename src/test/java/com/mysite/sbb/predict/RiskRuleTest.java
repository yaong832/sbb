package com.mysite.sbb.predict;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mysite.sbb.model.ModelInfo;

class RiskRuleTest {

    // 정상 평균: 온도 72.5, 압력 5.0, 진동 0.5, 가스 유량 117
    private static ModelInfo model() {
        ModelInfo model = new ModelInfo();
        model.setAvgTemperature(72.5);
        model.setAvgPressure(5.0);
        model.setAvgVibration(0.5);
        model.setAvgGasFlow(117.0);
        return model;
    }

    @Test
    @DisplayName("모든 값이 평균에 가까우면 정상, 0점")
    void allNearAverageIsNormal() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 5.0, 0.5, 117.0, model());

        assertEquals(0, result.score());
        assertEquals(RiskRule.NORMAL, result.label());
        assertEquals("정상 값과 데이터의 평균 기준에 가깝습니다.", result.reason());
    }

    @Test
    @DisplayName("가스 유량만 벗어나도 이상 (가스 300, 평균 117)")
    void gasFlowAloneIsAbnormal() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 5.0, 0.5, 300.0, model());

        assertEquals(20, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
        assertTrue(result.reason().contains("가스 유량"));
    }

    @Test
    @DisplayName("온도만 벗어나면 이상")
    void temperatureAloneIsAbnormal() {
        RiskRule.Result result = RiskRule.evaluate(95.0, 5.0, 0.5, 117.0, model());

        assertEquals(30, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }

    @Test
    @DisplayName("압력만 벗어나면 이상")
    void pressureAloneIsAbnormal() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 7.0, 0.5, 117.0, model());

        assertEquals(25, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }

    @Test
    @DisplayName("진동만 벗어나면 이상")
    void vibrationAloneIsAbnormal() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 5.0, 1.5, 117.0, model());

        assertEquals(25, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }

    @Test
    @DisplayName("허용 범위와 딱 같은 차이는 정상")
    void exactlyAtRangeIsNormal() {
        // 차이가 허용 범위와 같으면 '벗어남'이 아님 (초과일 때만 점수)
        RiskRule.Result result = RiskRule.evaluate(87.5, 6.5, 1.3, 157.0, model());

        assertEquals(0, result.score());
        assertEquals(RiskRule.NORMAL, result.label());
    }

    @Test
    @DisplayName("네 항목이 모두 벗어나면 100점")
    void allFourOutOfRangeScores100() {
        RiskRule.Result result = RiskRule.evaluate(50.0, 2.0, 2.0, 10.0, model());

        assertEquals(100, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }
}
