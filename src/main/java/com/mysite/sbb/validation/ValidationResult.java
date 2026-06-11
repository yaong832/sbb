package com.mysite.sbb.validation;

import java.time.LocalDateTime;

// JPA Entity 관련 import
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ValidationResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer totalCount;
	private Integer correctCount;
	private Integer wrongCount;
	private Double accuracy;
	private LocalDateTime validationDate;
	
	public Integer getId() {return id;}
	public Integer getTotalCount() {return totalCount;}
	public void setTotalCount(Integer totalCount) {this.totalCount = totalCount;}
	public Integer getCorrectCount() {return correctCount;}
	public void setCorrectCount(Integer correctCount) {this.correctCount = correctCount;}
	public Integer getWrongCount() {return wrongCount;}
	public void setWrongCount(Integer wrongCount) {this.wrongCount = wrongCount;}
	public Double getAccuracy() {return accuracy;}
	public void setAccuracy(Double accuracy) {this.accuracy = accuracy;}
	public LocalDateTime getValidationDate() {return validationDate;}
	public void setValidationDate(LocalDateTime validationDate) {this.validationDate = validationDate;}
	
}
