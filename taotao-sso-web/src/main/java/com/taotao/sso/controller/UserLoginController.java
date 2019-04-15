package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
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
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public  Object selectByToken(@PathVariable String token,String callback){
		//注入service
		//判断是否有回调请求(处理跨域)
		if(StringUtils.isNotBlank(callback)){
			MappingJacksonValue value = new MappingJacksonValue(userLoginService.selectByToken(token));
			value.setJsonpFunction(callback);
			return value;
		}
			
		return userLoginService.selectByToken(token);
		
	}
	/*@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback){
		
		//判断是否是Jsonp请求
		if(StringUtils.isNotBlank(callback)){
			//如果是jsonp 需要拼接 类似于fun({id:1});
			TaotaoResult result = userLoginService.selectByToken(token);
			String jsonpstr = callback+"("+JsonUtils.objectToJson(result)+")";
			return jsonpstr;
		}
		//如果不是jsonp
		//1.调用服务
		TaotaoResult result = userLoginService.selectByToken(token);
		return JsonUtils.objectToJson(result);
	}*/

	
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
