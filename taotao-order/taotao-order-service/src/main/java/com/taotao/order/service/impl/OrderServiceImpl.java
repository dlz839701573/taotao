package com.taotao.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.jedis.JedisClient;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired 
	private JedisClient  jedisClient;
	
	
	/*
	 *不能分别三个接收，前台传来的数据是OrderInfo对象(里边循环嵌套,订单条目，
	 *用户物流信息，最简单的方法就是创建一个OrderInfo对象来接受)
	 * 
	*/
	 
	 @Autowired 
	private TbOrderMapper  orderMapper;
	//注入订单条目表(一个商品的总个数，总价)
	@Autowired //注入订单项表(一个订单项有多个订单条目)
	private TbOrderItemMapper  orderItemMapper;
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	//初始订单id的key
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	//初始订单id的初始value
	@Value("${ORDER_ID_INIT}")
	private String ORDER_ID_INIT;
	//生成订单条目表id
	@Value("${ORDER_ITEM_ID_GEN_KEY}")
	private String ORDER_ITEM_ID_GEN_KEY;
	@Override
	public String createOrder(OrderInfo orderInfo) {
	//注入jedisClient
	//设置订单表中数据		
		//设置订单id
			//判断redis中初始订单id是否为空
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)){
			//不存在则生成一个初始订单id保存再redis中
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_INIT);
		}
			//初始订单自增并设置为当前订单id
		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
			//下次提交，重新检测(一定有上次自增过的订单id,再次自增设置为当前订单id)
		//设置实际付款金额
		orderInfo.setOrderId(orderId);
		orderInfo.setPostFee("0");
		//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orderInfo.setStatus(1);
		Date date = new Date();
		orderInfo.setCreateTime(date);
		orderInfo.setUpdateTime(date);
		// 3、向订单表插入数据。
		orderMapper.insert(orderInfo);
		
		//向订单详情表中插入数据(一个商品的总个数，总价)...(数据从前台传来包装到orderInfo中)
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			//生成明细id
			Long orderItemId = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY);
			tbOrderItem.setId(orderItemId.toString());
			tbOrderItem.setOrderId(orderId);
			//插入数据
			orderItemMapper.insert(tbOrderItem);
		}
		// 5、向订单物流表插入数据。
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(date);
		orderShipping.setUpdated(date);
		orderShippingMapper.insert(orderShipping);
		// 6、返回TaotaoResult。
		return orderId;
	}

}
/*order_id` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT                  //需要设置'订单id',   jedisClient生成
`payment` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分',
`payment_type` int(2) DEFAULT NULL COMMENT                                       //需要设置2、货到付款',
`post_fee` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT                              //需要设置'邮费。精确到2位小数;单位:元。如:200.07，表示:200元7分',
`status` int(10) DEFAULT NULL COMMENT                                           //需要设置  '状态：1、未付款（付款选择的是货到付款）
`create_time` datetime DEFAULT NULL COMMENT                                    //需要设置'订单创建时间',
`update_time` datetime DEFAULT NULL COMMENT '                                 //需要设置  订单更新时间',
`payment_time` datetime DEFAULT NULL COMMENT                                  //需要设置'付款时间', 
`consign_time` datetime DEFAULT NULL COMMENT                                  //需要设置'发货时间',
`end_time` datetime DEFAULT NULL COMMENT '交易完成时间',
`close_time` datetime DEFAULT NULL COMMENT '交易关闭时间',
`shipping_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '物流名称',       
`shipping_code` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '物流单号',
`user_id` bigint(20) DEFAULT NULL COMMENT                                             //需要设置   '用户id',
`buyer_message` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '买家留言',
`buyer_nick` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT                       //需要设置    '买家昵称',
`buyer_rate` int(2) DEFAULT NULL COMMENT '买家是否已经评价',*/