package com.taotao.content.service;

import java.util.List;

import javax.swing.text.AbstractDocument.Content;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {
	//查询指定广告模块列表
	public EasyUIDataGridResult selectContentList(Integer page, Integer rows,Long categoryId);
	//添加指定模块的具体内容
	public TaotaoResult saveContent(TbContent content);
	//添加前台首页轮播图广告模块列表
	public List<TbContent> selectContentList(Long categoryId);
}
