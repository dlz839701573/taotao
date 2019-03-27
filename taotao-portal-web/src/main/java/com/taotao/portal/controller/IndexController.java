package com.taotao.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.Module;
import com.taotao.common.util.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.Ad1Node;
//配置服务消费者
//配置扫描
@Controller
public class IndexController {
	
	
	//注入contentService对象
	@Autowired
	private ContentService contentService;
	
	@Value("${AD1_CATEGORY_ID}")
	private Long categoryId;
	
	@Value("${AD1_HEIGHT_B}")
	private String AD1_HEIGHT_B;
	
	@Value("${AD1_HEIGHT}")
	private String AD1_HEIGHT;
	
	@Value("${AD1_WIDTH}")
	private String AD1_WIDTH;
	
	@Value("${AD1_WIDTH_B}")
	private String AD1_WIDTH_B;
	
	
	//返回首页并从content中加载轮播图图片数据
	//请求的url:index
	//是否有参数传来:????
	//请求方式:无>>>>>>>(这里返回值类型，以及页面交互并不懂)
	//返回值类型:Model
	//传出的数据:????
	@RequestMapping("/index")
	public String ShowIndex(Model model){
		//得到数据
		List<TbContent> list = contentService.selectContentList(categoryId);
		//将数据依次取出并集合配置文件中的属性一并封装成另一个对象，并传给前台
		//自定义一个pojo(因为是临时使用，所以在web层定义一个即可)
		List<Ad1Node> nodes=new ArrayList<>();
		for (TbContent tbContent : list) {
			Ad1Node node = new Ad1Node();
			node.setAlt(tbContent.getSubTitle());
			node.setHeight(AD1_HEIGHT);
			node.setHeightB(AD1_HEIGHT_B);
			node.setHref(tbContent.getUrl());
			node.setSrc(tbContent.getPic());
			node.setSrcB(tbContent.getPic2());
			node.setWidth(AD1_WIDTH);
			node.setWidthB(AD1_WIDTH_B);
			nodes.add(node);
			System.out.println(node.toString());
			}
		model.addAttribute("ad1", JsonUtils.objectToJson(nodes));
		//返回到视图
		return "index";
		
	}
}
