package com.mysite.sbb;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	// 학습 전 추론·검증, 데이터 없이 학습 → 안내 화면
	@ExceptionHandler(NotReadyException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String notReady(NotReadyException e, Model model) {
		model.addAttribute("message", e.getMessage());
		model.addAttribute("nextUrl", e.getNextUrl());
		model.addAttribute("nextLabel", e.getNextLabel());
		return "notice";
	}
}
