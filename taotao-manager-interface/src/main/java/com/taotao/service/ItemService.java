package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {
	public EasyUIDataGridResult getItemList(Integer page,Integer rows);
	
	//根据商品的基础数据 和商品的描述信息 插入商品（插入商品基础表  和商品描述表）
	public TaotaoResult saveItem(TbItem item,String desc);
	
	//查询商品图片，接受的是商品的id，返回的是图片的链接
	
}
