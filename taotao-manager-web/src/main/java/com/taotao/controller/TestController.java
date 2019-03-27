package com.taotao.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.service.TestService;

@Controller
public class TestController {
	@Autowired
	private TestService testservice;
	/**
	 * 测试dubbo配置是否正常
	 * @return
	 */
	@RequestMapping("/test/queryNow")
	@ResponseBody
	public String queryNow(){
		return testservice.queryNow();
	}
}