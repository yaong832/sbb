package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SbbController {

	@GetMapping("/sbb1")
	@ResponseBody
	public String sbb1() {
		return "네 sbb입니다 환영할까요?";
	}
}

