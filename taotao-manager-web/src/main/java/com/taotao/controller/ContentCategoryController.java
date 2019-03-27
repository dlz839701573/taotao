package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
/**
 *内容分类处理
 * @author Administrator
 *
 */
@Controller
public class ContentCategoryController {
	
	@Autowired
	private ContentCategoryService contentCategoryService;
	//1.填写json返回的url
	//2盘对岸返回的数据是否是json对象
	
	
	@RequestMapping(value="/content/category/list",method=RequestMethod.GET)
	//因为返回的是一个json对象
	@ResponseBody
	public List<EasyUITreeNode>  getContentCategroyList(@RequestParam(value="id",defaultValue="0")  Long parentId){
		
		return contentCategoryService.getContentcateGoryList(parentId);
	}
	//是否注入service
	///content/category/create
	//method=post
	//参数
	//parentid:新增节点的父节点id
	//name:新增节点的name
	//返回值：TaotaoResult:包含分类id
	@RequestMapping(value="/content/category/create",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult createContentCateGory(Long parentId,String name){		
		TaotaoResult createContentCateGory = contentCategoryService.createContentCateGory(parentId, name);
		return createContentCateGory;
	}
	
	//修改分类名
	//是否注入service
	///content/category/update
	//判断返回的是否是json数据  否
	//请求方式：post  
	//参数
	//这里查看浏览器调试模式提示404 Not Found，但是数据没问题(不解)
	//>>>>>>返回值为json，null，忘记加@ResponseBody
	@RequestMapping(value="/content/category/update",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult updateContentCateGory(Long id,String name){
		contentCategoryService.updateContentCateGory(id, name);
		return TaotaoResult.ok();
	}
	
	//删除分类
	//是否注入mapper
	///content/category/delete/
	//传入的数据：id:是否与数据库中的一致？是
	//请求方式：post
	//是否需要传回数据：否
	@RequestMapping(value="/content/category/delete/",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult delectContentCateGory(Long id){
		
		contentCategoryService.delectContentCateGory(id);
		
		return TaotaoResult.ok();
		
	}
	
}
