package com.taotao.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.service.SearchService;

@Controller
public class SearchControler {
	
	/**
	 * 查询参数page(需要制定page)
	 * 查询条件queryString(前台传来的是q，需要进行转换)
	 * 需要返回的是Model视图
	 */
	@Autowired
	private SearchService  searchService;
	
	@Value("${ITEM_ROWS}")
	private Integer ITEM_ROWS;
	
	@RequestMapping("/search")
	public String search(@RequestParam("q")String queryString,
			@RequestParam(defaultValue="1")Integer page,
			Model model
			) throws Exception{
		queryString=new String(queryString.getBytes("iso-8859-1"),"utf-8");
		SearchResult result = searchService.search(queryString, page, ITEM_ROWS);
		//将数据传递到jsp页面中(第一个为jsp页面请求的数据，第二个为结果)
		System.out.println(result.toString());
		model.addAttribute("query", queryString);
		model.addAttribute("totalPages",result.getTotalCount());
		model.addAttribute("itemList",result.getItemList());
		model.addAttribute("page",page);
		return "search";

		
	}
	
}
