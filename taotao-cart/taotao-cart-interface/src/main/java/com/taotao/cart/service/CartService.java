package com.taotao.cart.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;

public interface CartService {
	/**根据商品的ID查询商品的信息
	 * @param userId 哪一个用户的购物车
	 * @param itemId 哪一个商品
	 * @param num  购买商品的数量
	 * @return
	 */
	public TaotaoResult addItemCart(Long userId,TbItem item ,Integer num);
	
	/**
	 * 根据用户ID和商品的ID查询是否存储在redis中
	 * @param userId
	 * @param itemId
	 * @return  null 说明不存在，如果不为空说明存在
	 */
	public TbItem queryTbItemByUserIdAndItemId(Long userId,Long itemId);
	/**
	 * 
	 * @param userId 根据用户id获取redis中购物车的数据
	 * @return List<TbItem>
	 */
	public List<TbItem> queryCartListByUserId(Long userId);
	/**
	 * 
	 * @param userId 根据用户id和商品id，更新(重新设置商品数目)
	 * @param itemId
	 * @param num
	 * @return TaotaoResult
	 */
    public TaotaoResult updateItemCartByItemId(Long userId,Long itemId,Integer num);
    /**
     * 删除指定商品
     * @param userId 用户id
     * @param itemId 商品id
     * @return  TaotaoResult
     */
    public TaotaoResult delItemCart(Long userId,Long itemId);
    
    
}
