package com.taotao.sso.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	
	@RequestMapping("/")
	public String showIndex(){
		return "login";
	}
	
	@RequestMapping("/page/{page}")
	public String showPage(@PathVariable String page,String redirect,Model model){
		System.out.println(redirect);
		model.addAttribute("redirect", redirect);
		return page;
	}
	
}
