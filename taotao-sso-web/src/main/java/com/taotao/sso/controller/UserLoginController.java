package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.remoting.http.HttpServer;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.sso.service.UserLoginService;

@Controller
public class UserLoginController {
	/**POST
	 * http://sso.taotao.com/user/login
	 * @param username 用户名
	 * @param password 密码
	 * @return TaotaoResult
	 */
	@Autowired
	private UserLoginService  userLoginService;
	@Value("${TT_TOOKEN_KEY}")
	private String TT_TOOKEN_KEY;
	
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public  TaotaoResult Login(HttpServletRequest request,HttpServletResponse response,
			String username,String password){
		
		//注入mapper
		//调用服务
		TaotaoResult result = userLoginService.login(username, password);
		//如果登陆成功则设置cookie
		if(result.getStatus()==200){
			//设置Cookie的值 不设置生效时间默认浏览器关闭即失效,也不编码
			CookieUtils.setCookie(request, response, TT_TOOKEN_KEY, result.getData().toString());
			
		}
		return result;
		
	}
	
	/**GET
	 * /user/token/{token}
	 * @param token 用户登录凭证
	 * @return
	 */
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET)
	@ResponseBody
	public  TaotaoResult selectByToken(@PathVariable String token){
		//注入service
		return userLoginService.selectByToken(token);
		
	}
	/**GET
	 * /user/logout/{token}
	 * @param token 安全退出凭证
	 * @return
	 */
	@RequestMapping(value="/user/logout/{token}",method=RequestMethod.GET)
	@ResponseBody
	public  TaotaoResult loginOut(@PathVariable String token){
		//注入service
		return userLoginService.loginOut(token);
		
	}
}
