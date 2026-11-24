package com.mysite.sbb.predict;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void 모든_값이_평균에_가까우면_정상() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 5.0, 0.5, 117.0, model());

        assertEquals(0, result.score());
        assertEquals(RiskRule.NORMAL, result.label());
        assertEquals("정상 값과 데이터의 평균 기준에 가깝습니다.", result.reason());
    }

    @Test
    void 가스_유량만_벗어나도_이상() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 5.0, 0.5, 300.0, model());

        assertEquals(20, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
        assertTrue(result.reason().contains("가스 유량"));
    }

    @Test
    void 온도만_벗어나면_이상() {
        RiskRule.Result result = RiskRule.evaluate(95.0, 5.0, 0.5, 117.0, model());

        assertEquals(30, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }

    @Test
    void 압력만_벗어나면_이상() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 7.0, 0.5, 117.0, model());

        assertEquals(25, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }

    @Test
    void 진동만_벗어나면_이상() {
        RiskRule.Result result = RiskRule.evaluate(72.5, 5.0, 1.5, 117.0, model());

        assertEquals(25, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }

    @Test
    void 허용_범위와_딱_같은_차이는_정상() {
        // 차이가 허용 범위와 같으면 '벗어남'이 아님 (초과일 때만 점수)
        RiskRule.Result result = RiskRule.evaluate(87.5, 6.5, 1.3, 157.0, model());

        assertEquals(0, result.score());
        assertEquals(RiskRule.NORMAL, result.label());
    }

    @Test
    void 네_항목이_모두_벗어나면_100점() {
        RiskRule.Result result = RiskRule.evaluate(50.0, 2.0, 2.0, 10.0, model());

        assertEquals(100, result.score());
        assertEquals(RiskRule.ABNORMAL, result.label());
    }
}
