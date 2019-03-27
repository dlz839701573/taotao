package com.taotao.controller;

import javax.swing.text.AbstractDocument.Content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;

//配置扫描
@Controller
public class ContentController {
	@Autowired
	private ContentService  contentService;
	
	//查询content列表
	//是否注入mapper
	///content/query/list
	//请求方式:get
	//传入的参数page,row,categoryId
	//返回值类型pageHelper
	@RequestMapping(value="/content/query/list",method=RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult selectContent(Integer page, Integer rows,Long categoryId){
		return contentService.selectContentList(page, rows, categoryId);
	}
	
	//添加content数据
	//是否注入
	///content/save
	//请求值类型:$.post("/content/save",$("#contentAddForm").serialize(), function(data)
	//传入的数据:Tbcontent对象(注意这里不是content)
	//返回值:TaotaoResult:data.status == 200
	
	@RequestMapping(value="/content/save",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult saveContent(TbContent content){
		return contentService.saveContent(content);
		
	}
}
