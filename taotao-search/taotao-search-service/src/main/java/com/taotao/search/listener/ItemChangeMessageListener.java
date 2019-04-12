package com.taotao.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.search.service.SearchService;
/**
 * 接受消息的监听器
 * @author Administrator
 *
 */
public class ItemChangeMessageListener implements MessageListener {

	@Autowired
	private SearchService searchService;
	//获取消息，判断消息的类型是否是TextMessage
	//如果是则获取商品id
	//根据商品id在数据库中查询商品
	//将查询到的商品更新到索引库
	@Override
	public void onMessage(Message message) {
		if(message instanceof TextMessage){
			System.out.println("taotao-seach接收到MQ的更新索引");
			TextMessage textMessage = (TextMessage)message;
			try {
				Long itemId = Long.parseLong(textMessage.getText());
				//调用searchService方法更新索引库
				searchService.updateSearch(itemId);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

}
