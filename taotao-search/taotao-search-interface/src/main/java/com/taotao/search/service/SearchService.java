package com.taotao.search.service;

import java.util.List;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;

public interface SearchService {
	//导入索引库
	public TaotaoResult importAllSearchItems() throws Exception;
	//删除索引库
	public TaotaoResult DeImportAllSearchItems()throws Exception;
	//条件查询索引
	public SearchResult search(String queryString,Integer page,Integer rows)throws Exception;
}
