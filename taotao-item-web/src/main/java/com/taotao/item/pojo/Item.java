package com.taotao.item.pojo;



import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.taotao.pojo.TbItem;

public class Item extends TbItem {
	//将Tbitem中的数据拷贝到Item对象中
	public Item(TbItem item){
		BeanUtils.copyProperties(item, this);
	}
	
	
	//接收父类的image
	public String[] getImages(){
		//判断非空则切割返回数组
		if(StringUtils.isNotBlank(super.getImage())){
			return super.getImage().split(",");
		}
		//空则返回空
		return null;
	}
	
	
	
}
