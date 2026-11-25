package com.mysite.sbb.predict;

//날짜와 시간을 사용하기 위한 import
import java.time.LocalDateTime;

//List 사용을 위한 import
import java.util.List;

//Spring Service import
import org.springframework.stereotype.Service;

//알람 기능 import
import com.mysite.sbb.alarm.EquipmentAlarmService;

//모델 정보 import
import com.mysite.sbb.model.ModelInfo;
import com.mysite.sbb.model.ModelService;

@Service
public class PredictionService {

    private final PredictionResultRepository predictionResultRepository;
    private final ModelService modelService;
    private final EquipmentAlarmService equipmentAlarmService;

    public PredictionService(PredictionResultRepository predictionResultRepository,
                             ModelService modelService,
                             EquipmentAlarmService equipmentAlarmService) {
        this.predictionResultRepository = predictionResultRepository;
        this.modelService = modelService;
        this.equipmentAlarmService = equipmentAlarmService;
    }

    public List<PredictionResult> getList() {
        return this.predictionResultRepository.findAll();
    }

    public PredictionResult getResult(Integer id) {
        return this.predictionResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("결과 데이터를 찾을 수 없습니다."));
    }

    public PredictionResult predict(String equipmentName, Double temperature, Double pressure,
                                    Double vibration, Double gasFlow) {
        ModelInfo model = this.modelService.getLatestModel();
        PredictionResult result = makePrediction(equipmentName, temperature, pressure, vibration, gasFlow, model);
        PredictionResult saved = this.predictionResultRepository.save(result);

        if (RiskRule.ABNORMAL.equals(saved.getResult())) {
            this.equipmentAlarmService.createAuto(equipmentName, saved.getReason(), saved.getId());
        }

        return saved;
    }

    public PredictionResult makePrediction(String equipmentName, Double temperature, Double pressure,
                                           Double vibration, Double gasFlow, ModelInfo model) {

        RiskRule.Result rule = RiskRule.evaluate(temperature, pressure, vibration, gasFlow, model);

        PredictionResult predictionResult = new PredictionResult();
        predictionResult.setEquipmentName(equipmentName);
        predictionResult.setTemperature(temperature);
        predictionResult.setPressure(pressure);
        predictionResult.setVibration(vibration);
        predictionResult.setGasFlow(gasFlow);
        predictionResult.setResult(rule.label());
        predictionResult.setRiskScore(rule.score());
        predictionResult.setReason(rule.reason());
        predictionResult.setPredictDate(LocalDateTime.now());

        return predictionResult;
    }

    public void addFeedback(Integer id, String userFeedback, String feedbackMemo) {
        PredictionResult result = getResult(id);
        result.setUserFeedback(userFeedback);
        result.setFeedbackMemo(feedbackMemo);
        this.predictionResultRepository.save(result);
    }
}