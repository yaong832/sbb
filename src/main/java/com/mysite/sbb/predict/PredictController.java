package com.mysite.sbb.predict;

//Spring Controller 관련 import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/predict")
public class PredictController {
	
	private final PredictionService predictionService;
	
	public PredictController(PredictionService predictionService) {
		this.predictionService = predictionService;
	}
	
	@GetMapping("/form")
	public String form(){
		return "predict_form";
	}
	
	@PostMapping("/run")
	public String create(@RequestParam("equipmentName") String equipmentName,
			@RequestParam("temperature") Double temperature,
			@RequestParam("pressure") Double pressure,
			@RequestParam("vibration") Double vibration,
			@RequestParam("gasFlow") Double gasFlow) {
		PredictionResult result = this.predictionService.predict(equipmentName,temperature,pressure,vibration,gasFlow);
		return "redirect:/predict/result/" + result.getId();
	}
	
	@GetMapping("/result/{id}")
	public String action(Model model, @PathVariable("id") Integer id) {
		model.addAttribute("result",this.predictionService.getResult(id));
		return "predict_detail";
	}
	
	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("resultList", this.predictionService.getList());
		return "predict_list";
	}
	
	@PostMapping("/feedback/{id}")
	public String feedback(@PathVariable("id") Integer id,
			@RequestParam("userFeedback") String userFeedback,
			@RequestParam("feedbackMemo") String feedbackMemo){
		this.predictionService.addFeedback(id, userFeedback, feedbackMemo);
		return "redirect:/predict/result/" + id;
	}
}
