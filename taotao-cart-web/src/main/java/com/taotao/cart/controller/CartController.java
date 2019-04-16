package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.service.ItemService;
import com.taotao.sso.service.UserLoginService;



@Controller
public class CartController {

	@Value("${TT_TOOKEN_KEY}")
	private String TT_TOOKEN_KEY;
	@Value("${TT_CART_KEY}")
	private String TT_CART_KEY;
	@Value("${TT_CART_EXPIRE_TIME}")
	private Integer TT_CART_EXPIRE_TIME;
	@Autowired
	private CartService cartService;
	@Autowired
	private UserLoginService userLoginService;
	@Autowired
	private ItemService  itemService;
	/**
	 * 添加到购物车
	 * @param itemId 
	 * @param num
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/cart/add/{itemId}",method=RequestMethod.GET)
	public String addCart(@PathVariable Long itemId, Integer num,HttpServletRequest request,HttpServletResponse response){
		//注入service
		//判断用户是否登陆(从cookie中获取)
		String token  = CookieUtils.getCookieValue(request, TT_TOOKEN_KEY);
		//对比redis中的cookie
		TaotaoResult tokenResult = userLoginService.selectByToken(token);
		if(tokenResult.getStatus() == 200){
			//说明已经登陆
			TbUser user = (TbUser) tokenResult.getData();
			//获取商品数据
			TbItem tbItem = itemService.selectItemById(itemId);
			cartService.addItemCart(user.getId(), tbItem, num);
		}else{
			//1.获取本地购物车
			String cookieValue = CookieUtils.getCookieValue(request, TT_CART_KEY, true);
				//1.1判断是否为空(获取list集合，
			List<TbItem> list = new ArrayList();
			if(StringUtils.isNotBlank(cookieValue)){
				list = JsonUtils.jsonToList(cookieValue, TbItem.class);
				
			}
				//不管是否为空都转换成list集合，
				//后续添加商品时，找到就添加数目，找不到商品重新设置属性进行添加)
				
				//1.1.1如果为空，则直接创建创建一个List<Item>,放入后转换为Json格式字符串即可
			//定义一个标志,如果遍历到指定商品，则返回信号true:重新将list集合设置到cookie中
			//如果返回false，则说明,没有遍历到商品,则需要利用商品id从服务层获取商品,设置数量，设置默认显示的图片，重新传给list>>
			boolean flog=false;
			for (TbItem tbItem : list) {
				if(tbItem.getId()==itemId.longValue()){
					tbItem.setNum(num+tbItem.getNum());
					flog=true;
					break;
				}
			}
			if(flog==true){
				String listItemjson = JsonUtils.objectToJson(list);
				CookieUtils.setCookie(request, response, TT_CART_KEY, listItemjson, TT_CART_EXPIRE_TIME, true);

			}else{//cookie中没有该商品
				TbItem item=itemService.selectItemById(itemId);
				item.setNum(num);
				if(StringUtils.isNotBlank(item.getImage())){
					item.setImage(item.getImage().split(",")[0]);
				}
				
				list.add(item);
				
				String listItemjson = JsonUtils.objectToJson(list);
				CookieUtils.setCookie(request, response, TT_CART_KEY, listItemjson, TT_CART_EXPIRE_TIME, true);
			}
			
				//1.1.2如果不为空.则
			//2.从购物车中取出保存在本地的数据
					//2.1将数据转换成List<Tbitem>对象
					//2.2遍历判断集合中是否有指定id的item,
			//3.如果本地已经存在该商品
					//3.1获取指定商品数目加一
						//3.1.1将商品转换成对象
			//4.如果本地不存在该商品
			//5.将数据的商品id存储到数据库(key为TT_CART_KEY,值为商品List<Tbitem>)
					//设置商品图片为默认第一个，设置商品数目为num
					//获取原有List集合,添加一个item
					//重新将list转换成json字符串，
					//从新设置cookie
		}
		
		return "cartSuccess";
		
	}
	
	

	@RequestMapping("/cart/cart")
	public String getCartList(HttpServletRequest request){
		//判断是否登录
			//调用本地cookie信息(key)
		String cookieValue = CookieUtils.getCookieValue(request, TT_TOOKEN_KEY);
		   //获取远程信息(value)
		TaotaoResult token = userLoginService.selectByToken(cookieValue);
		if(token.getStatus()==200){
			// 从cookie中获取用户的token信息
			TbUser user = (TbUser)token.getData();
			//调用服务层方法获取购物车
			List<TbItem> cartList = cartService.queryCartListByUserId(user.getId());
			
			request.setAttribute("cartList", cartList);
		}else{//查看本地cookie中的购物车
				String cookieValue2 = CookieUtils.getCookieValue(request, TT_CART_KEY, "utf-8");
				if(StringUtils.isNotBlank(cookieValue2)){
					List<TbItem> listItem = JsonUtils.jsonToList(cookieValue2, TbItem.class);
					request.setAttribute("cartList", listItem);
				}
				
			
		}
		//登陆提取数据库端
		return "cart";
		
	}
	/*"/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val()*/
	/**
	 * http://localhost:8089/cart/update/num/1207791403/4.action
	 * @param num 更新后的商品数目！！
	 * @param itemId 商品id
	 * @return TaotaoResult
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateCartNum(@PathVariable Long itemId,@PathVariable Integer num,HttpServletRequest request,HttpServletResponse response ){
		//判断是否登录
		//调用本地cookie信息(key)
		String cookieValue = CookieUtils.getCookieValue(request, TT_TOOKEN_KEY);
		   //获取远程信息(value)
		TaotaoResult token = userLoginService.selectByToken(cookieValue);
		if(token.getStatus()==200){
			// 从cookie中获取用户的token信息
			TbUser user = (TbUser)token.getData();
			cartService.updateItemCartByItemId(user.getId(),itemId,num);
			return TaotaoResult.ok();
		}else{//重新设置本地cookie中指定商品
			String cookieValue2 = CookieUtils.getCookieValue(request, TT_CART_KEY, "utf-8");
			if(StringUtils.isNotBlank(cookieValue2)){
				List<TbItem> listItem = JsonUtils.jsonToList(cookieValue2, TbItem.class);
				//定义一个标志，找到则返回true，则需要重新将数据存到Cookies中
				boolean flog=false;
				for (TbItem tbItem : listItem) {
					if(tbItem.getId()==itemId.longValue()){
						tbItem.setNum(num);
						flog=true;
						break;
					}
				}
				if(flog==true){
					String listItemjson = JsonUtils.objectToJson(listItem);
					CookieUtils.setCookie(request, response, TT_CART_KEY, listItemjson, TT_CART_EXPIRE_TIME, true);

				}
				
				return TaotaoResult.ok();
			}else{
				//为空(无商品)(不可能)不处理
			}
		}
		
		return null;
		}
	/*实现删除购物车商品
	功能分析：点击删除，根据商品的id及用户的id从redis中删除即可。*/
	//cart/delete/${cart.id}.html
	@RequestMapping("/cart/delete/{cartId}")
	public String delItemCart(@PathVariable Long cartId,HttpServletRequest request,HttpServletResponse response) {
		//查看用户是否登录
		String cookieValue = CookieUtils.getCookieValue(request, TT_TOOKEN_KEY);
		TaotaoResult token = userLoginService.selectByToken(cookieValue);
		if(token.getStatus()==200){
			
			
			//已经登陆。执行删除逻辑
			TbUser user= (TbUser)token.getData();
			cartService.delItemCart(user.getId(), cartId);
		}else{
			String cookieValue2 = CookieUtils.getCookieValue(request, TT_CART_KEY, "utf-8");
			if(StringUtils.isNotBlank(cookieValue2)){
				List<TbItem> listItem = JsonUtils.jsonToList(cookieValue2, TbItem.class);
				//定义一个标志，找到则返回true，则需要删除指定数据后重新将集合存到Cookies中
				boolean flog=false;
				for (TbItem tbItem : listItem) {
					if(tbItem.getId()==cartId.longValue()){
						listItem.remove(tbItem);
						flog=true;
						break;
					}
				}
				if(flog==true){
					String listItemjson = JsonUtils.objectToJson(listItem);
					CookieUtils.setCookie(request, response, TT_CART_KEY, listItemjson, TT_CART_EXPIRE_TIME, true);

				}
				
			}
		}
		return "redirect:/cart/cart.html";
	}

	
	

}
