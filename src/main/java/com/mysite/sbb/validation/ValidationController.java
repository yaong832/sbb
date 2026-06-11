package com.mysite.sbb.validation;

//Optional 사용을 위한 import
import java.util.Optional;

//Spring Controller 관련 import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/validation")
public class ValidationController {
	private ValidationService validationService;
	
	public ValidationController(ValidationService validationService) {
		this.validationService = validationService;
	}
	
	@GetMapping("/run")
	public String run() {
		this.validationService.runValidation();
		return "redirect:/validation/result";
	}
	
	@GetMapping("/result")
	public String result(Model model) {
		Optional<ValidationResult> result = this.validationService.getLatestResultOptional();
		model.addAttribute("validationResult", result.orElse(null));
		return "validation_result";
	}
}

