package com.taotao.order.pojo;

import java.io.Serializable;
import java.util.List;

import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

public class OrderInfo extends TbOrder implements Serializable {
	
	/**
	 * 一个订单绑定一个人收货人,所以收货人的id和订单id是一个id只有一个
然后是商品详情表，一个人买多个商品《List集合》,其中包括商品的价格和数量,而买完需要给
收货人发货，所以一个订单就得有一个对应的收货人的地址等联系方式《一个TbOrderShipping对象》
。这些都需要在一个订单中完成，所以重新创建了一个订单的继承类,添加了其他两个表的信息,而在插入数据库时，则采用循环遍历取出

	 */
	private List<TbOrderItem> orderItems;
	
	private TbOrderShipping  orderShipping;
	
	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}
	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
}
