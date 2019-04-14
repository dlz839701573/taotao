package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	
	@RequestMapping("/")
	public String showIndex(){
		return "login";
	}
	
	@RequestMapping("page/{page}")
	public String showItemList(@PathVariable String page){
		return page;
	}
	
	
	
}
