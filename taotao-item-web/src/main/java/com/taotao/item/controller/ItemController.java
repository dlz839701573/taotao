package com.taotao.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;
/**
 * 商品详情页
 * @author Administrator
 *
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	// (/item/${item.id })
	
	@RequestMapping("/item/{itemId}")
	public String getItem(@PathVariable  Long itemId,Model model){
		TbItem tbItem = itemService.selectItemById(itemId);
		TbItemDesc tbItemDesc = itemService.selectItemDescById(itemId);
		
		Item item = new Item(tbItem);
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", tbItemDesc);
		
		return "item";
		
	}
}
