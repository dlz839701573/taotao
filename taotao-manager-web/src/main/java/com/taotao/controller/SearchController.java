package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.service.SearchService;

//配置扫描
//注入
//消费者引入服务
@Controller
public class SearchController {
	@Autowired
	private SearchService  searchService;
	
	
	@RequestMapping("/index/importall")
	@ResponseBody
	public TaotaoResult  Importment() throws Exception{
		searchService.importAllSearchItems();
		return TaotaoResult.ok();
	}
}
