package com.godfathercapybara.capybara.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.godfathercapybara.capybara.repository.UserRepository;
import com.godfathercapybara.capybara.service.ValidateService;

@Controller
public class UserWebController {
	@Autowired
	private ValidateService validateService;
	@Autowired
	private UserRepository capybaraRepository;

	@GetMapping("/login")
	public String login() {
		return "login";
	}      

	@GetMapping("/loginerror")
	public String loginerror() {
		return "loginerror";
	}

	@GetMapping("/private")
	public String privatePage() {
		return "private";
	}
}
