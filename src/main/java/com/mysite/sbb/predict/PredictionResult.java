package com.mysite.sbb.predict;

//날짜와 시간을 사용하기 위한 import
import java.time.LocalDateTime;

//JPA Entity 관련 import
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PredictionResult {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String equipmentName;
	private Double temperature;
	private Double pressure;
	private Double vibration;
	private Double gasFlow;
	private String result;
	private Integer riskScore;
	
	@Column(columnDefinition = "TEXT")
	private String reason;
	private String userFeedback;
	
	@Column(columnDefinition = "TEXT")
	private String feedbackMemo;

	private LocalDateTime predictDate;
	
	public Integer getId() {return id;}
	public String getEquipmentName() {return equipmentName;}
	public void setEquipmentName(String equipmentName) {this.equipmentName = equipmentName;}
	public Double getTemperature() {return temperature;}
	public void setTemperature(Double temperature) {this.temperature = temperature;}
	public Double getPressure() {return pressure;}
	public void setPressure(Double pressure) {this.pressure = pressure;}
	public Double getVibration() {return vibration;}
	public void setVibration(Double vibration) {this.vibration = vibration;}
	public Double getGasFlow() {return gasFlow;}
	public void setGasFlow(Double gasFlow) {this.gasFlow = gasFlow;}
	public String getResult() {return result;}
	public void setResult(String result) {this.result = result;}
	public Integer getRiskScore() {return riskScore;}
	public void setRiskScore(Integer riskScore) {this.riskScore = riskScore;}
	
	public String getReason() {return reason;}
	public void setReason(String reason) {this.reason = reason;}
	public String getUserFeedback() {return userFeedback;}
	public void setUserFeedback(String userFeedback) {this.userFeedback = userFeedback;}
	public String getFeedbackMemo() {return feedbackMemo;}
	public void setFeedbackMemo(String feedbackMemo) {this.feedbackMemo = feedbackMemo;}
	public LocalDateTime getPredictDate() {return predictDate;}
	public void setPredictDate(LocalDateTime predictDate) {this.predictDate = predictDate;}
	
}
