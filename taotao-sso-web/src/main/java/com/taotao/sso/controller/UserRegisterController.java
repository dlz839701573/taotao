package com.taotao.sso.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserRegisterService;

/**
 * 注册以及效验相关
 * @author 陈宁
 *
 */
@Controller
public class UserRegisterController {
	@Autowired
	private UserRegisterService userRegister;
	/**
	 * //user/check/{param}/{type}
	 * @param param 
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/user/check/{param}/{type}",method=RequestMethod.GET)
	@ResponseBody
	public TaotaoResult ChackData(@PathVariable String param, @PathVariable Integer type){
		//1.注入service
		//2.调用服务
		String param1 = null;
		try {
			param1 = new String(param.getBytes("ISO8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userRegister.ChackData(param1, type);
		
	}
	
	/**
	 * 注册接口
	 * 请求方法	POST
	 * url  http://sso.taotao.com/user/register
	 * @param username 不能为空   并且 唯一
	 * @param password 不能为空  可以重复  （加密存储）	
	 * @param phone  可以为空  不能重复 （如果不为空，就不能重复）
	 * @param email 可以为空   不能重复 （如果不为空，就不能重复）
	 * @return TaotaoResult
	 */
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult Register(TbUser user){
		//1.注入service
		//2.调用服务
		TaotaoResult register = userRegister.Register(user);
		return register;
	}
	
	
	
}
