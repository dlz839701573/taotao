package com.taotao.cart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.cart.jedis.JedisClient;
import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;

@Service
public class CartServiceImpl implements CartService{
	@Autowired
	private JedisClient jedisClient;

	@Value("${TT_CART_REDIS_PRE_KEY}")
	private String TT_CART_REDIS_PRE_KEY;
	
	
	@Override
	public TaotaoResult addItemCart(Long userId, TbItem item, Integer num) {
		//1.注入jedisClient客户端
		//2.注入mapper
		//3.创建查询条件
		//5.查看该用户购物车内是否已经有该商品
		TbItem item1 = queryTbItemByUserIdAndItemId(userId,item.getId());
		if(item1==null){
			//如果没有，则将商品直接添加到购物车
			//先将商品数目置为0
			item.setNum(1);
			//设置第一张图片显示出去
			item.setImage(item.getImage().split(",")[0]);
			String itemJson = JsonUtils.objectToJson(item);
			//键为表名+用户id,field为用户id,value为商品属性
			jedisClient.hset(TT_CART_REDIS_PRE_KEY+":"+userId, item.getId()+"",itemJson);
		}else{
			//如果有，则增加商品数量
			item1.setNum(num+item1.getNum());
			String itemJson = JsonUtils.objectToJson(item1);
			//重新存入redis中
			jedisClient.hset(TT_CART_REDIS_PRE_KEY+":"+userId, item.getId()+"",itemJson);
		}
		return TaotaoResult.ok();
	}
	
	
	@Override
	public TbItem queryTbItemByUserIdAndItemId(Long userId, Long itemId) {
		//查看该用户购物车内是否已经有该商品
		String hget = jedisClient.hget(TT_CART_REDIS_PRE_KEY+":"+userId, itemId+"");
		if(StringUtils.isNotBlank(hget)){
			//已经有该商品
			TbItem item = JsonUtils.jsonToPojo(hget, TbItem.class);
			return item;
		}else{
			//
			return null;
		}
		//没有该商品
		
	}

	//根据用户id获取redis的购物车
	@Override
	public List<TbItem> queryCartListByUserId(Long userId) {
		//1.注入jedisClien  //查询所有的hash类型的列表
		Map<String, String> map = jedisClient.hgetAll(TT_CART_REDIS_PRE_KEY+":"+userId);
		//这里不明白为什么这么取出
		Set<Entry<String, String>> set = map.entrySet();
		if(set!=null){//判断是否为空
			List<TbItem> list = new ArrayList<>();
			for (Entry<String, String> entry : set) {
				//将该用户购物车中的商品json字符串转换成对象
				TbItem item = JsonUtils.jsonToPojo(entry.getValue(), TbItem.class);
				list.add(item);
			}
			return list;
		}
		//3.从cookie中获取用户信息
		return null;
	}


	@Override
	public TaotaoResult updateItemCartByItemId(Long userId, Long itemId, Integer num) {
		//1.查看购物车是否有该商品,有则返回该商品
		TbItem item = queryTbItemByUserIdAndItemId(userId, itemId);
		if(item!=null){
			//如果有，则重新设置商品数量
			item.setNum(num);
			String itemJson = JsonUtils.objectToJson(item);
			//重新存入redis中
			jedisClient.hset(TT_CART_REDIS_PRE_KEY+":"+userId, item.getId()+"",itemJson);
		}else{////如果没有。。。。。不管啦
			
		}
		return TaotaoResult.ok();
	}


	@Override
	public TaotaoResult delItemCart(Long userId, Long itemId) {
		//1.注入jedisClient
		jedisClient.hdel(TT_CART_REDIS_PRE_KEY+":"+userId, itemId+"");
		//2.删除
		
		return TaotaoResult.ok();
	}

}
