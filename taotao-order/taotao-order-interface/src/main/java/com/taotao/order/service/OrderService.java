package com.taotao.order.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.pojo.OrderInfo;

public interface OrderService {
	/**
	 * 创建订单
	 * @return 只需要返回一个订单id即可，其他的存入数据库中  
	 */
	public String createOrder(OrderInfo orderInfo);
	
}
