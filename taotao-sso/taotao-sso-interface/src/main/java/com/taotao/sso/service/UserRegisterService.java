package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

/**
 * 用户注册登录接口
 * @author 陈宁
 *
 */
public interface UserRegisterService {
	/**
	 * 根据参数和类型来效验参数
	 * @param param 要校验的数据
	 * @param type 1，2，3分别代表username、phone、email
	 * @return 
	 */
	public TaotaoResult ChackData(String param,Integer type);
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	public TaotaoResult Register(TbUser user);
}
