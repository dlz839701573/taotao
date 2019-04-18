package com.taotao.order.controller;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserLoginService;

@Controller
public class OrderController {
	
	//获取本地购物车数据
	@Value("${TT_CART_KEY}")
	private String TT_CART_KEY;
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserLoginService userLoginService;
	
	
	//http://localhost:8092/order/order-cart.html
	@RequestMapping("/order/order-cart")
	public String  showOrderCart(HttpServletRequest request){
		//以下有登陆拦截器验证
		//登陆拦截设置的用户信息key为USER_INFO
		TbUser user= (TbUser)request.getAttribute("USER_INFO");
		
		/*//1获取用户本地cookie
		String cookieValue = CookieUtils.getCookieValue(request, TT_TOOKEN_KEY);
		//2判断是否为空
		if(StringUtils.isNotBlank(cookieValue)){
			//调用sso系统验证用户是否登陆
			TaotaoResult token = userLoginService.selectByToken(cookieValue);
			if(token.getStatus()==200){
				//用户已经登陆
				//获取用户购物车信息(注入CartService)
				TbUser user = (TbUser)token.getData();
				List<TbItem> cartList = cartService.queryCartListByUserId(user.getId());
				//将数据传递到前台
				request.setAttribute("cartList", cartList);
				return "order-cart";
			}
		}*/
		
		//根据用户信息从redis中获取用户购物车中的商品数据
		List<TbItem> cartList = cartService.queryCartListByUserId(user.getId());
		//获取本地购物车信息
		String cookieValue2 = CookieUtils.getCookieValue(request, TT_CART_KEY, true);
		
		//将商品信息保存在request域中
		//跳转到/order/order-cart
		
		//进入以下步骤说明用户已经登陆成功
		//获取用户信息request域中获取，以及需要跳转到的网址
		//根据用户登录信息查看用户redis中购物车
		//查看本地购物车
		//合并购物车
		
		
		//获取用户购物车信息(注入CartService)
		
		//获取本地cookie中购物车的商品
		
		/*List<TbItem> itemList=new ArrayList();
		//如果不为空
		if(StringUtils.isNotBlank(cookieValue2)){
			//用于
			
			itemList= JsonUtils.jsonToList(cookieValue2, TbItem.class);
			
			for (TbItem tbItem : itemList) {
				
			}
		
		}
*/
		//并添加到redis中
		//删除本地cookies
		
		
		//将数据传递到前台
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
	@RequestMapping(value="/order/create", method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo, HttpServletRequest request){
		// 1、接收表单提交的数据OrderInfo。
				// 2、补全用户信息。
		TbUser user = (TbUser) request.getAttribute("USER_INFO");
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());

		String orderId= orderService.createOrder(orderInfo);
		// a)需要Service返回订单号
		request.setAttribute("orderId", orderId);
		request.setAttribute("payment", orderInfo.getPayment());
		// b)当前日期加三天。
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		request.setAttribute("date", dateTime.toString("yyyy-MM-dd"));
		// 4、返回逻辑视图展示成功页面
		return "success";
	
	}
}
