package com.taotao.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;



/**
 * 接受消息的监听器
 * @author Administrator
 *
 */
public class ItemChangeGenHTMLMessageListener  implements MessageListener {

	@Autowired
	private ItemService  itemService;
	
	@Autowired
	private FreeMarkerConfigurer  config;
	
	
	//获取消息，判断消息的类型是否是TextMessage
 	//如果是则获取商品id
	//根据商品id在数据库中查询商品
	//将查询到的商品更新到索引库

	@Override
	public void onMessage(Message message) {
		System.out.println("监听到更改或添加商品的信号，开始生成静态页面");
		
		if(message instanceof TextMessage){
			TextMessage textMessage=(TextMessage)message;
			try {
				String text = textMessage.getText();
				long itemId = Long.parseLong(text);
				TbItem item = itemService.selectItemById(itemId);
				TbItemDesc itemDesc = itemService.selectItemDescById(itemId);
				this.getHTML(item,itemDesc);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private void getHTML(TbItem item, TbItemDesc itemDesc) throws Exception{
		//得到FreeMarkerConfigurer的版本以及编码等其它信息
		Configuration configuration=config.getConfiguration();
		//2.创建模板 获取模板文件对象
		Template template = configuration.getTemplate("item.ftl");
		//创建数据集
		Map model = new HashMap<>();
		model.put("item", item);
		model.put("itemDesc", itemDesc);
		//4.输出
		Writer writer = new FileWriter(new File("D:\\freemarker\\item"+"\\"+item.getId()+".html"));
		// 7.调用模板对象中的方法进行输出
				// 参数1.指定数据集
				// 参数2.指定生成的文件的全路径（FileWrtier来包装）
				// 8.流关闭

		template.process(model, writer);
		//5.关闭流
		writer.close();
	}
	
}
