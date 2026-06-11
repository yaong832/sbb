package com.mysite.sbb.model;

import java.time.LocalDateTime;
// 여러 개의 데이터를 저장하기 위한 List
import java.util.List;
// Optional 사용을 위한 import
import java.util.Optional;
// Spring Service import
import org.springframework.stereotype.Service;
// SensorData 관련 import
import com.mysite.sbb.sensor.SensorData;
import com.mysite.sbb.sensor.SensorDataService;

@Service
public class ModelService {
	
	private final ModelInfoRepository modelInfoRepository;
	private final SensorDataService sensorDataService;
	
	public ModelService(ModelInfoRepository modelInfoRepository, SensorDataService sensorDataService) {
		this.modelInfoRepository = modelInfoRepository;
		this.sensorDataService = sensorDataService;
	}
	
	public ModelInfo train() {
		List<SensorData> normalDataList = this.sensorDataService.getNormalData();
		
		if (normalDataList.isEmpty()) {
			throw new RuntimeException("정상 학습 데이터가 없습니다.");
		}
		
		double avgTemperature = normalDataList.stream().mapToDouble(SensorData::getTemperature).average().orElse(0);
		double avgPressure = normalDataList.stream().mapToDouble(SensorData::getPressure).average().orElse(0);
		double avgVibration =  normalDataList.stream().mapToDouble(SensorData::getVibration).average().orElse(0);
		double avgGasFlow =  normalDataList.stream().mapToDouble(SensorData::getGasFlow).average().orElse(0);
		
		ModelInfo modelInfo = new ModelInfo();
		modelInfo.setAvgTemperature(avgTemperature);
		modelInfo.setAvgPressure(avgPressure);
		modelInfo.setAvgVibration(avgVibration);
		modelInfo.setAvgGasFlow(avgGasFlow);
		modelInfo.setTrainDataCount(normalDataList.size());
		modelInfo.setTrainDate(LocalDateTime.now());
		
		return this.modelInfoRepository.save(modelInfo);
	}
	
	public Optional<ModelInfo> getLatestModelOptional(){
		return this.modelInfoRepository.findTopByOrderByIdDesc();
	}
	public ModelInfo getLatestModel() {
	    return this.modelInfoRepository.findTopByOrderByIdDesc()
	            .orElseThrow(() -> new RuntimeException("학습된 모델이 없습니다."));
	}
}
