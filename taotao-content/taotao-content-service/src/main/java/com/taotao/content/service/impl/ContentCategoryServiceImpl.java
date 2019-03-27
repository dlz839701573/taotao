package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.pojo.TbContentExample;
/**
 * 内容分类
 * @author Administrator
 *
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired 
	private TbContentCategoryMapper CategoryMapper;
	@Autowired 
	private TbContentMapper contentMapper;
	@Override
	public List<EasyUITreeNode> getContentcateGoryList(Long parentId) {
		//注入mapper
		//创建example
		TbContentCategoryExample example=new TbContentCategoryExample();
		//设置条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		
		//查询
		List<TbContentCategory> list = CategoryMapper.selectByExample(example);
		
		/*将查询到的对象封装进easyui树里边，所以要先创建一个List里边装有Easyui对象*/
		List<EasyUITreeNode> nodes=new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node=new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			nodes.add(node);
		}
		
		return nodes;
	}
	/**
	 * Long parentId父节点的id，name为前台传入的name
	 * 新增分类
	 */
	@Override
	public TaotaoResult createContentCateGory(Long parentId, String name) {
		//注入mapper
		//创建一个数据对象
		TbContentCategory category=new TbContentCategory();
		/* category.setId(IDUtils.genItemId());主键自增*/
		category.setName(name);
		//父类id
		category.setParentId(parentId);
		//创建时间
		category.setCreated(new Date());
		//更新时间
		category.setUpdated(new Date());
		//是否是父节点
		category.setIsParent(false);
		
		category.setStatus(1);
		category.setSortOrder(1);
		//指定查询条件（需要返回主键id，修改mapper.xml映射文件）
		CategoryMapper.insert(category);
		
		//根据父节点的id获得父节点，判断是否是父节点，若不是则更新其为父节点
		TbContentCategory parent=CategoryMapper.selectByPrimaryKey(parentId);
		if(parent.getIsParent()==false){
			//重新设置新增节点的父节点
			parent.setIsParent(true);
			//更新数据局
			CategoryMapper.updateByPrimaryKeySelective(parent);
		}
		return TaotaoResult.ok(category);
	}
	/*
	 * 更新节点
	 * 确定前台传入的数据：id:node.id(用于查找数据),
	 * name:node.text(前段传回的修改后的name，需要写入数据库)
	 * 确定返回值类型  
	 * 
	 */
	@Override
	public void updateContentCateGory(Long id, String name) {
		//注入mapper
		//获取传入id的对象
		TbContentCategory contentCategory = CategoryMapper.selectByPrimaryKey(id);
		//将传入的对象名传入数据库
		contentCategory.setName(name);
		CategoryMapper.updateByPrimaryKeySelective(contentCategory);

	}
	
	/***
	 * 删除节点
	 *获取节点,判断是否是父节点？是。不允许删除
	 *不是父节点，删除指定内容和内容下的所有具体内容
	 *判断父节点是否还有子节点，是，停止
	 *否，改变父节点的isparent=false
	 * 
	 * 
	 * 
	 */
	@Override
	public void delectContentCateGory(Long id) {
		//注入mapper
		TbContentCategory contentCategory = CategoryMapper.selectByPrimaryKey(id);
		//这里对是父节点的不给与处理，只处理非父节点
		if(contentCategory.getIsParent()==false){
			//删除下边栏目的内容对应的是Category表中的数据
				//引入contentMapper
				//因为删除条件非主键，所以需要设置条件
			TbContentExample example=new TbContentExample();
			com.taotao.pojo.TbContentExample.Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(id);
			contentMapper.deleteByExample(example);
			
			//删除节点
			CategoryMapper.deleteByPrimaryKey(id);
			
			//判断父节点是否还是父节点,
			//先得到父节点id,查找同一父节点id数量
			Long parentId = contentCategory.getParentId();
			TbContentCategoryExample  example2=new TbContentCategoryExample();
			Criteria  criteria2 =example2.createCriteria();
			criteria2.andParentIdEqualTo(parentId);
			int count = CategoryMapper.countByExample(example2);
			
			if(count<1){
				//得到父节点
				TbContentCategory FucontentCategory = CategoryMapper.selectByPrimaryKey(parentId);
				FucontentCategory.setIsParent(false);
			}
		}
		
	
	
	}
	
	
}
