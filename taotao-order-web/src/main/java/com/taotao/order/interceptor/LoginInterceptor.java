package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.sso.service.UserLoginService;

@Controller
public class LoginInterceptor implements HandlerInterceptor {

	//redis中购物车key
	@Value("${TT_TOOKEN_KEY}")
	private String TT_TOOKEN_KEY;
	//SSO系统用户登陆网址
	@Value("${SSO_URL}")
	private String SSO_URL;
	//本地cookie中购物车key
	@Value("${TT_CART_KEY}")
	private String TT_CART_KEY;
	@Autowired
	private CartService cartService;
	@Autowired
	private UserLoginService userLoginService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//获取用户当前访问路径
	    String URL = request.getRequestURL().toString();
		
		//1获取用户本地登陆相关cookie
		String cookieValue = CookieUtils.getCookieValue(request, TT_TOOKEN_KEY);
		//2判断是否为空
		if(StringUtils.isEmpty(cookieValue)){//重定向到登陆界面(附带用户访问路径格式url?redict=)
			System.out.println("本地cookie为空");
			response.sendRedirect(SSO_URL+"/page/login?redirect="+URL);
			
			//表示不用继续往下走了
			//这里一定要返回一个结果！！！！！
			return false;
		}
		//往下走说明本地用户登陆cookie存在
		//调用sso系统验证用户是否登陆
		TaotaoResult token = userLoginService.selectByToken(cookieValue);
		if(token.getStatus()!=200){//如果用户登陆信息过期
			System.out.println("远程cookie为空");
			//重定向到登陆界面(附带用户访问路径格式url?redict=
			response.sendRedirect(SSO_URL+"/page/login?redirect="+URL);

			
			
			//这里一定要返回一个结果！！！！！不然报错到你怀疑人生
			return false;
		}
		System.out.println(token.getData().toString());//往下走说明用户已经登陆
		//设置 保存用户访问信息到request中	
		request.setAttribute("USER_INFO", token.getData());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
