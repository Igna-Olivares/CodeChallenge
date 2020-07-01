package com.iolivares.codeChallenge.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	private static final String REDIRECT_SWAGGER = "redirect:swagger-ui.html";

	@RequestMapping("/")
	public String home() {
		return REDIRECT_SWAGGER;
	}
}
