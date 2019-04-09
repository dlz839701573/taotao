package com.taotao.search.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Destination;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;


@Repository
public class SearchDao {
	//更新索引相关
	@Autowired
	private  SolrServer solrServer;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Resource
	private Destination destination; 
	
	@Autowired
	private SearchItemMapper mapper;
	//根据条件查询数据,返回的数据有总页数，总记录数，商品列表信息，数据回显
	//是一个List<SearchItem>
	//传入的数据是分页信息(一页20或者N条记录)
	//检查分页数据中的起始数据是否是1，不是的话赋值为1，
	//page默认页面为1
	public SearchResult QueryAll(SolrQuery query) throws Exception{
		//将返回的对象注入进
		SearchResult searchResult=new SearchResult();
		//条件
		//执行查询
		QueryResponse queryResponse = solrServer.query(query);
		
		//设置高亮
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		
		//取出查询结果
		SolrDocumentList results = queryResponse.getResults();
		
		//将总记录数传入SearchResult中
		searchResult.setTotalCount(results.getNumFound());
		
		//打印共查询到商品数量
		System.out.println("共查询到商品数量:" + results.getNumFound());
		
		//将查询到的商品数据放到list中后赋值给searchResult
		List<SearchItem> list=new ArrayList<>();
		
		
		
		
		for (SolrDocument solrDocument : results) {
			SearchItem item = new SearchItem();
			
			//Integer类型强制转换为String不能通过
			//这里什么不直接强转
			item.setId(Long.parseLong(solrDocument.get("id").toString()));
			
			
			item.setSell_point(solrDocument.get("item_sell_point").toString());
			 
			item.setPrice((Long) solrDocument.get("item_price"));
			
			item.setImage(solrDocument.get("item_image").toString());
			//前端界面不需要显示 
		//	item.setItem_desc((String) solrDocument.get("_version_"));
			
			item.setCategory_name(solrDocument.get("item_category_name").toString());
			
			//设置高亮(得到的其实还是title？？？？？？？)
			List<String> list2 = highlighting.get(solrDocument.get("id")).get("item_title");
					
			//高亮显示非常的迷
			String gaoliang="";
			if(list2!=null&& list2.size()>0){
				//有高亮
				gaoliang=list2.get(0);
			}else{
				gaoliang=solrDocument.get("item_title").toString();
			}
			
			item.setTitle(gaoliang);
			list.add(item);
		}
		searchResult.setItemList(list);
		//因为这里得到的searchResult对象的pageCount所以需要在service层完善	
		return searchResult;
		//创建一个query对象
	}
	
	//根据商品id获取商品数据，将获取到的数据更新到索引库中
	public TaotaoResult getSearchItemId(long itemId) throws Exception, IOException{
		//注入mapper
		SearchItem searchItem = mapper.getSearchItemId(itemId);
		//获取数据

		//利用solrj 更新索引
				//创建连接
		SolrInputDocument document = new SolrInputDocument();
				//创建一个文档对象
				//向文档中添加域
		document.addField("id", searchItem.getId());
		document.addField("item_title", searchItem.getTitle());
		document.addField("item_sell_point", searchItem.getSell_point());
		document.addField("item_price", searchItem.getPrice());
		document.addField("item_image", searchItem.getImage());
		document.addField("item_category_name", searchItem.getCategory_name());
		document.addField("item_desc", searchItem.getItem_desc());
				//把document对象添加到索引库中
		solrServer.add(document);
				//提交修改
		solrServer.commit();
		
		
		
		return TaotaoResult.ok();
		
	}
	
	
}
