package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;

public interface ContentCategoryService {
	//淘淘商城内容分类管理
	public List<EasyUITreeNode> getContentcateGoryList(Long id);
	//淘淘商城新增节点
	public TaotaoResult createContentCateGory(Long parentId,String name);
	//更新节点
	public void updateContentCateGory(Long id,String name);
	
	//
	public void delectContentCateGory(Long id);
}
