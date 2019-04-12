package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.manager.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.service.ItemService;

/**
 * 查询所有商品
 * 商品的增加以及查询单个商品的操作
 * 查询单个商品时，将数据添加至redis中,并设置过期时间,每访问一次，重新设置过期时间，保证频繁访问的商品在啊redis中，增速
 * 增加商品的同时利用消息队列MQ广播(更新索引库,生成商品详情的静态页面)
 * @author 陈宁
 *
 */

@Service
public class ItemServiceImpl implements ItemService  {
@Autowired
private TbItemMapper itemMapper;
//mapper属性没有注入在向数据库中插入数据时 回报空指针异常
@Autowired
private TbItemDescMapper itemDescMapper;
//注入MQ相关
@Autowired
private  JmsTemplate jmsTemplate;
@Resource
private Destination destination;
//注入缓存相关
@Autowired
private JedisClient jedisClient;
//key前缀(表=名)
@Value("${item_infi_key}")
private String item_infi_key;
//key后缀(字段名)
@Value("${item_infi_expire}")
private int item_infi_expire;

	/**
	 * 查询所有商品
	 */
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

	//根据商品id查询商品
	//查看缓存中是否有数据，没有则从数据库中取出并返回，然后添加缓存
	//注入
	@Override
	public TbItem selectItemById(Long itemId) {
		//注入mapper
		
		//查看缓存中是否有数据
		//缓存操作需要try catch
		try {
			if (itemId != null) {
				if (jedisClient.get(item_infi_key + ":" + itemId + ":base") != null) {
					String string = jedisClient.get(item_infi_key + ":" + itemId + ":base");
					//查询此商品调用次数多说明是热 门商品，需要增加过期时间
					//后缀不应该写死，这里写死了
					jedisClient.expire(item_infi_key + ":" + itemId + ":base", item_infi_expire);
					System.out.println("从缓存中取出TbItem成功");
					return JsonUtils.jsonToPojo(string, TbItem.class);
					
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		//根据主键查询商品详情
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		try {
			if (item != null){
			//添加缓存
			jedisClient.set(item_infi_key + ":" + itemId + ":base", JsonUtils.objectToJson(item));
			//添加过期时间
			jedisClient.expire(item_infi_key + ":" + itemId + ":base", item_infi_expire);
			System.out.println("TbItem添加缓存成功");
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//返回items
		return item;
	}
	//商品desc
	@Override
	public TbItemDesc selectItemDescById(Long itemId) {
		//注入descmapper
		
		//查看缓存中是否有数据
		try {
			if(itemId!=null){
				if(jedisClient.get(item_infi_key+":"+itemId+":desc")!=null){
					//后缀不应该写死，这里写死了
					String desc = jedisClient.get(item_infi_key+":"+itemId+":desc");
					//查询此商品调用次数多说明是热门商品，需要增加过期时间
					jedisClient.expire(item_infi_key + ":" + itemId + ":base", item_infi_expire);
					
					System.out.println("从缓存中取出TbItemDesc成功");
					return JsonUtils.jsonToPojo(desc, TbItemDesc.class);
				}
			}
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		
		//从数据库中查询数据
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		//将数据添加到缓存
		try {
			if(itemDesc!=null){
				//添加缓存
				jedisClient.set(item_infi_key+":"+itemId+":desc", JsonUtils.objectToJson(itemDesc));
				//设置过期时间
				jedisClient.expire(item_infi_key+":"+itemId+":desc",item_infi_expire);
				System.out.println("TbItemDesc添加缓存成功");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return itemDesc;
	}


	
}
