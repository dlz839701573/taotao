package com.taotao.search.service;

import java.util.List;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.TaotaoResult;

public interface SearchService {
	public TaotaoResult importAllSearchItems() throws Exception;

	public TaotaoResult DeImportAllSearchItems()throws Exception;
}
