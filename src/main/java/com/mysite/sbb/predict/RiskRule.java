package com.mysite.sbb.predict;

//모델 정보 import
import com.mysite.sbb.model.ModelInfo;

/**
 * 센서값이 정상 평균에서 얼마나 벗어났는지로 위험 점수를 매기는 판정 규칙.
 * 추론(PredictionService)과 검증(ValidationService)이 모두 이 클래스를 써서
 * 두 곳의 판정 결과가 항상 같게 한다.
 */
public final class RiskRule {

    public static final String NORMAL = "NORMAL";
    public static final String ABNORMAL = "ABNORMAL";

    // 항목별 허용 범위(정상 평균과의 차이)와 가중치. 가중치 합계는 100
    public static final double TEMPERATURE_RANGE = 15;
    public static final double PRESSURE_RANGE = 1.5;
    public static final double VIBRATION_RANGE = 0.8;
    public static final double GAS_FLOW_RANGE = 40;

    public static final int TEMPERATURE_WEIGHT = 30;
    public static final int PRESSURE_WEIGHT = 25;
    public static final int VIBRATION_WEIGHT = 25;
    public static final int GAS_FLOW_WEIGHT = 20;

    // 가장 작은 가중치(가스 유량 20)와 같게 두어, 한 항목이라도 범위를 벗어나면 ABNORMAL
    public static final int ABNORMAL_SCORE = 20;

    private RiskRule() {
    }

    public static Result evaluate(Double temperature, Double pressure, Double vibration, Double gasFlow,
                                  ModelInfo model) {

        int score = 0;
        StringBuilder reason = new StringBuilder();

        if (Math.abs(temperature - model.getAvgTemperature()) > TEMPERATURE_RANGE) {
            score += TEMPERATURE_WEIGHT;
            reason.append("온도가 정상 평균에서 크게 벗어났습니다. ");
        }

        if (Math.abs(pressure - model.getAvgPressure()) > PRESSURE_RANGE) {
            score += PRESSURE_WEIGHT;
            reason.append("압력이 정상 평균에서 크게 벗어났습니다. ");
        }

        if (Math.abs(vibration - model.getAvgVibration()) > VIBRATION_RANGE) {
            score += VIBRATION_WEIGHT;
            reason.append("진동 값이 정상 평균에서 크게 벗어났습니다. ");
        }

        if (Math.abs(gasFlow - model.getAvgGasFlow()) > GAS_FLOW_RANGE) {
            score += GAS_FLOW_WEIGHT;
            reason.append("가스 유량이 정상 평균에서 크게 벗어났습니다. ");
        }

        if (reason.length() == 0) {
            reason.append("정상 값과 데이터의 평균 기준에 가깝습니다.");
        }

        String label = score >= ABNORMAL_SCORE ? ABNORMAL : NORMAL;
        return new Result(score, label, reason.toString().trim());
    }

    /** 위험 점수(0~100), 판정(NORMAL/ABNORMAL), 판정 사유 */
    public record Result(int score, String label, String reason) {
    }
}
