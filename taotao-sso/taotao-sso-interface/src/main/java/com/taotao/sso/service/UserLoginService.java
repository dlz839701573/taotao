package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * 用户登录逻辑
 * @author 陈宁
 *
 */
public interface UserLoginService {
	/**
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @return TaotaoResult 登陆成功返回200和token，失败返回400
	 */
	public TaotaoResult login(String username,String password);
	/**
	 * 通过Token(key)取出保存在redis中的值
	 * @param token 
	 * @return  TaotaoResult
	 */
	public TaotaoResult selectByToken(String token);
	/**
	 * 通过Token(key)删除保存在redis中的值(删除远程的value，可以防止他人利用本地cookie登陆)
	 * @param token
	 * @return
	 */
	public TaotaoResult loginOut(String token);
	
}
