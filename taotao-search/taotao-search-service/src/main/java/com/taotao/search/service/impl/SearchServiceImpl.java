package com.taotao.search.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchService;


//注入service
//注入mapper
//发布服务
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer; 
	
/*	@Override
	public TaotaoResult importAllSearchItems() {
		//注入mapper
		//查询到的数据需要交给solrj
		List<SearchItem> searchItemList = searchItemMapper.getSearchItemList();
		
		
		//链接solrj服务器
	//	SolrServer solrServer=new HttpSolrServer("http://39.96.180.131/solr");
		//创建一个文档对象
		SolrInputDocument document=new SolrInputDocument();
		//向文档中添加域
		for (SearchItem searchItem : searchItemList) {
			searchItem.getId();
			searchItem.getTitle();
			searchItem.getImage();
			searchItem.getPrice();
			searchItem.getCategory_name();
			searchItem.getItem_desc();
			searchItem.getSell_point();
			//id类型需要传入字符串(!!!!!!!!!!!!!!!!!!!)
			document.addField("id", searchItem.getId().toString());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_category_name", searchItem.getCategory_name());
			document.addField("item_desc", searchItem.getItem_desc());
			document.addField("item_sell_point", searchItem.getSell_point());
			//把document对象添加到索引库中
			try {
				solrServer.add(document);
				solrServer.commit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//第一个参数：域的名称，域的名称必须是在schema.xml中定义的
		//第二个参数：域的值
		//把document对象添加到索引库中
		//提交
		//solrj将数据传递给solr服务器
		

		return TaotaoResult.ok();
	}

}
*/
	
	
	@Override
	public TaotaoResult importAllSearchItems() throws Exception{
		// 1、查询所有商品数据。
		List<SearchItem> itemList = searchItemMapper.getSearchItemList();
		// 2、创建一个SolrServer对象。
		// 3、为每个商品创建一个SolrInputDocument对象。
		for (SearchItem searchItem : itemList) {
			SolrInputDocument document = new SolrInputDocument();
			// 4、为文档添加域
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			document.addField("item_desc", searchItem.getItem_desc());
			// 5、向索引库中添加文档。
			solrServer.add(document);
		}
		//提交修改
		solrServer.commit();
		// 6、返回TaotaoResult。
		return TaotaoResult.ok();
	}
}