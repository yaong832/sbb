package com.mysite.sbb.validation;

//날짜와 시간을 사용하기 위한 import
import java.time.LocalDateTime;
//List 사용을 위한 import
import java.util.List;
//Optional 사용을 위한 import
import java.util.Optional;
//Spring Service import
import org.springframework.stereotype.Service;
//모델 정보 import
import com.mysite.sbb.model.ModelInfo;
import com.mysite.sbb.model.ModelService;
//판정 규칙 import
import com.mysite.sbb.predict.RiskRule;
//센서 데이터 import
import com.mysite.sbb.sensor.SensorData;
import com.mysite.sbb.sensor.SensorDataService;

@Service
public class ValidationService {

    private final ValidationResultRepository validationResultRepository;
    private final SensorDataService sensorDataService;
    private final ModelService modelService;

    public ValidationService(ValidationResultRepository validationResultRepository,
                             SensorDataService sensorDataService,
                             ModelService modelService) {

        this.validationResultRepository = validationResultRepository;
        this.sensorDataService = sensorDataService;
        this.modelService = modelService;
    }

    public ValidationResult runValidation() {

        List<SensorData> dataList = this.sensorDataService.getList();
        ModelInfo model = this.modelService.getLatestModel();

        int total = dataList.size();
        int correct = 0;

        for (SensorData data : dataList) {
            String predicted = predictLabel(data, model);

            if (predicted.equals(data.getLabel())) {
                correct++;
            }
        }

        int wrong = total - correct;
        double accuracy = total == 0 ? 0 : (correct * 100.0 / total);

        ValidationResult result = new ValidationResult();
        result.setTotalCount(total);
        result.setCorrectCount(correct);
        result.setWrongCount(wrong);
        result.setAccuracy(accuracy);
        result.setValidationDate(LocalDateTime.now());

        return this.validationResultRepository.save(result);
    }

    private String predictLabel(SensorData data, ModelInfo model) {
        // 추론 화면과 같은 판정 규칙을 사용
        return RiskRule.evaluate(data.getTemperature(), data.getPressure(),
                data.getVibration(), data.getGasFlow(), model).label();
    }

    public Optional<ValidationResult> getLatestResultOptional() {
        return this.validationResultRepository.findTopByOrderByIdDesc();
    }
}
