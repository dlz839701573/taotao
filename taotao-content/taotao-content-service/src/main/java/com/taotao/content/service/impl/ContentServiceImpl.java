package com.taotao.content.service.impl;
//首页轮播图
import java.util.Date;
import java.util.List;

import javax.swing.text.AbstractDocument.Content;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClientPool;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
//配置扫描
@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	private JedisClientPool pool;
	//注入mapper
	//发布服务
	@Autowired
	private TbContentMapper  contentMapper;
	
	@Value("${CONTENT_KEY}")
	private String  CONTENT_KEY;
	//查询广告列表
	@Override
	public EasyUIDataGridResult selectContentList(Integer page, Integer rows, Long categoryId) {
		//对page进行赋值(赋值为1，防止page为空)
		//对rows进行赋值(赋值为20，防止rows为空)
		if(page==null)page=1;
		if(rows==null)rows=30;
		//设置分页信息 
		PageHelper.startPage(page, rows);
		
		//根据categoryId(非主键)查询content列表
		//创建查询条件对象
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		//输入条件
		criteria.andCategoryIdEqualTo(categoryId);
		//得到所查询的对象
		List<TbContent> list = contentMapper.selectByExample(example);
		//分页配置
		PageInfo<TbContent> info=new PageInfo<>(list);
		
		//封装到EasyUIDataGridResult
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal((int)info.getTotal());
		return result; 
		
		
	}
	//添加指定模块的广告
	//是否注入mapper:是
	//是否发布服务:是
	//传入的数据是对象，需要添加创建时间以及更新时间
	//添加数据，需要删除缓存重新添加
	@Override
	public TaotaoResult saveContent(TbContent content) {
		try {
			pool.hdel(CONTENT_KEY, content.getCategoryId()+"");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insertSelective(content);
		return TaotaoResult.ok();
	}
	
	//查询前台首页轮播图广告
	//是否注入mapper
	//是否发布服务:是
	//传入的数据:categoryId(非主键)
	//创建查询对象
/*	查询内容列表时添加缓存。
	1、查询数据库之前先查询缓存。
	2、查询到结果，直接响应结果。
	3、查询不到，缓存中没有需要查询数据库。
	4、把查询结果添加到缓存中。
	5、返回结果。

	向redis中添加缓存：
	Key：categoryId  field
	Value：内容列表。需要把java对象转换成json。

	使用hash对key进行归类。
	HASH_KEY:HASH
	            |--KEY:VALUE
	            |--KEY:VALUE
	            |--KEY:VALUE
	            |--KEY:VALUE


	注意：添加缓存不能影响正常业务逻辑。*/
	@Override
	public List<TbContent> selectContentList(Long categoryId) {
		//搭建redis集群缓存
		//注入redis连接池
		//将id传入连接池看是否为空
		try {
		String jsonstr = pool.hget(CONTENT_KEY, categoryId+"");
		if(StringUtils.isNotBlank(jsonstr)){
			//非空>>>将数据从连接池取出返回给前台
			System.out.println("这里有 缓存");
			return JsonUtils.jsonToList(jsonstr, TbContent.class);
	
			}
		}catch (Exception e1) {
			e1.printStackTrace();
		}

		//根据categoryId(非主键)查询content列表
		//创建查询条件对象
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		//输入条件
		criteria.andCategoryIdEqualTo(categoryId);
		//得到所查询的对象
		List<TbContent> list = contentMapper.selectByExample(example);
	
		try {
		//空>>>>>将查询到的数据保存进连接池，
				pool.hset(CONTENT_KEY, categoryId+"", JsonUtils.objectToJson(list));
				System.out.println("添加缓存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
		
	}

}