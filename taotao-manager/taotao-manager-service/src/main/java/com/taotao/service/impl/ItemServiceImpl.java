package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.service.ItemService;
@Service
public class ItemServiceImpl implements ItemService  {
@Autowired
private TbItemMapper itemMapper;
//mapper属性没有注入在向数据库中插入数据时 回报空指针异常
@Autowired
private TbItemDescMapper itemDescMapper;

@Autowired
private  JmsTemplate jmsTemplate;
@Resource
private Destination destination;
	
	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		if(page==null)page=1;
		if(rows==null)rows=30;
		//设置分页查询信息
		PageHelper.startPage(page, rows);
		//创建查询对象不需要查询条件 
		TbItemExample example = new TbItemExample();
		//从数据库中查出的数据封装到list中
		List<TbItem> list = itemMapper.selectByExample(example);
		//获取分页信息
		PageInfo<TbItem> info = new PageInfo<>(list);
		//封装到EasyUIDataGridResult
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		/*result.setRows(info.getList());*/
		result.setRows(list);
		result.setTotal((int)info.getTotal());
		return result;
		
	}

/**
 * 补全商品详细信息
 * 商品id，状态，创建时间
 * 
 */
	@Override
	public TaotaoResult saveItem(TbItem item, String desc) {
		// 1、生成商品id
		final long itemId = IDUtils.genItemId();
		// 2、补全TbItem对象的属性
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		// 3、向商品表插入数据
		itemMapper.insert(item);
		// 4、创建一个TbItemDesc对象
		TbItemDesc itemDesc = new TbItemDesc();
		// 5、补全TbItemDesc的属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		System.out.println(itemDesc);
		// 6、向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
			//由于下边的是一个多线程，所以（第一次执行）可能上边的还没执行下边的就执行了，这个时候获取的itemId为空就会报空指针异常
		//以后出现的概率小是因为第一次加载会将数据存入缓存中去
		//将更改或添加的商品id发送至MQ，由MQ发送给索引逻辑更新索引
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				
				return session.createTextMessage(itemId+"");
			}
		});
		return TaotaoResult.ok();
	}


	
}
