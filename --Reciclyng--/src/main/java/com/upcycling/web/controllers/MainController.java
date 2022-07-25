package com.upcycling.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class MainController {

	@GetMapping("/home")
	public ModelAndView home() {
		
		ModelAndView model = new ModelAndView("support-customers");
		return model;
	}
}
