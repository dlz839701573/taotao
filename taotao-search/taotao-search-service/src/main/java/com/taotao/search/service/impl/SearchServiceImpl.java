package com.taotao.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.dao.SearchDao;
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
	
	@Autowired
	private SearchDao searchDao;
/*	@Override
	public TaotaoResult importAllSearchItems() {
		
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

	@Override
	public TaotaoResult DeImportAllSearchItems() throws Exception {
		//删除所有索引
		solrServer.deleteByQuery("*:*");
		//提交修改
		solrServer.commit();
		return TaotaoResult.ok();
	}
	
	/**
	 * 显示查询结果
	 * 
	 */
	
	//注入mapper
	//将查询到的数据进行包装
	//传入的参数？      1.查询内容String queryString
	//			2.查询内容一页几行Integer rows
	//          3.从第几页开始显示Integer page
				//计算开始数据位置(page-1)*rows
	//		返回的参数一个对象SearchResult
	
	@Override
	public SearchResult search(String queryString, Integer page, Integer rows) throws Exception {
		//1.创建一个SearchQuery对象
		SolrQuery query = new SolrQuery();
		//2.设置主查询条件以及搜索的域
		//设置默认搜索的域
		query.set("df","item_keywords");
		//设置主查询条件
		if(queryString!=null){
			query.setQuery(queryString);
		}else{
			query.set("*:*");
		}
		

		//3.设置过滤条件,设置分页
		if(page==null)page=1;
		if(rows==null)rows=60;
		
		query.setRows(rows);
		//数据开始位置
		query.setStart((page-1)*rows);   
		/*//4.设置高亮
				//开启高亮
		query.setHighlight(true);
				//设置高亮域
		query.addHighlightField("item_title");
				//设置前后缀
		query.setHighlightSimplePre("<span style='color:red'>");
		query.setHighlightSimplePost("</span>");*/
		
		//2.2.设置默认的搜索域
				//2.3设置高亮
				query.setHighlight(true);
				query.setHighlightSimplePre("<em style=\"color:red\">");
				query.setHighlightSimplePost("</em>");
				query.addHighlightField("item_title");//设置高亮显示的域
		
		
		
		//执行查询
		SearchResult result = searchDao.QueryAll(query);
		// 7、需要计算总页数=总记录数除以每页记录数~~~
		long recordCount = result.getTotalCount();
		//完善 totalCount/总记录数pageCount
		
		
		Long  pageCount=  recordCount/rows;
		if(recordCount%rows>0){
			pageCount++;
		}
		result.setPageCount(pageCount);
		
		return result;
	}
	
	
}