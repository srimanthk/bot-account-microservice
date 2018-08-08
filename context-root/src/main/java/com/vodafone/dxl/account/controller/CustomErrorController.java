package com.vodafone.dxl.account.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vodafone.dxl.exception.custom.ResourceNotFoundException;
@Controller
public class CustomErrorController implements ErrorController {
	
	@RequestMapping("/error")
	public Object fallback(){
		throw new ResourceNotFoundException();
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}

}
