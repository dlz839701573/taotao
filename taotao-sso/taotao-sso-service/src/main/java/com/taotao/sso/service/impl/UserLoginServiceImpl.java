package com.taotao.sso.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.jedis.JedisClient;
import com.taotao.sso.service.UserLoginService;
@Service
public class UserLoginServiceImpl implements UserLoginService {
	@Autowired
	private TbUserMapper mapper;
	@Autowired JedisClient jedisClient;
	@Value("${USER_INFO}")
	private String USER_INFO;
	@Value("${EXPIRE}")
	private Integer EXPIRE;
	
	@Override
	public TaotaoResult login(String username, String password) {
		//1.注入mapper
		//2.判断用户名和密码是否为空
		if(StringUtils.isEmpty(username)){
			return TaotaoResult.build(400, "用户名不能为空");
		}
		if(StringUtils.isEmpty(password)){
			return TaotaoResult.build(400, "密码不能为空");
		}
		//3.将输入的密码使用MD5加密后从数据库中查询用户名和密码看是否匹配
		String password1 = DigestUtils.md5DigestAsHex(password.getBytes());
			//从数据库中查询
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//查询条件
		criteria.andUsernameEqualTo(username);
		//根据查询条件查询用户
		List<TbUser> list = mapper.selectByExample(example);
		if(list==null||list.size()==0){
			return TaotaoResult.build(400, "用户名不存在");
		}
		//将输入的密码加密后对比数据库的密码
		TbUser tbUser = list.get(0);
		String password2 = tbUser.getPassword();
		
		String token = UUID.randomUUID().toString();
		if(!password1.equals(password2)){
			return TaotaoResult.build(400, "密码错误");
		}
		//4.密码正确，登陆成功.保存登陆信息到redis
		//将用户信息转成Json数据保存在redis中，键为token(uuid),
		//值为将密码设置为空之后的用户信息,并把key保存在本机中（并设置cookie关闭销毁）
		tbUser.setPassword(null);
		String userJson = JsonUtils.objectToJson(tbUser);
		 jedisClient.set(USER_INFO+":"+token, userJson);
		//5.设置session过期时间，半小时内有效
		 jedisClient.expire(USER_INFO+":"+token, EXPIRE);
		 return TaotaoResult.ok(token);
		
	}

	@Override
	public TaotaoResult selectByToken(String token) {
			
		//1.注入jedisClient
		String userJson = jedisClient.get(USER_INFO+":"+token);
		System.out.println(userJson);
		//通过键查询有值，//如果查询到的有值则增加过期时间,返回200和用户信息
		if(StringUtils.isNotBlank(userJson)){
			jedisClient.expire(USER_INFO+":"+token, EXPIRE);
			//如果查询到用户信息，则返回200和用户信息(用户信息转换成对象)
			TbUser user = JsonUtils.jsonToPojo(userJson, TbUser.class);
			return TaotaoResult.ok(user);
		}
		//如果不存在返回400
		return TaotaoResult.build(400, "用户信息不存在");
	}

	@Override
	public TaotaoResult loginOut(String token) {
		//1.注入jedisClient
		String userJson = jedisClient.get(USER_INFO+":"+token);
		//通过键查询有值，//如果查询到的有值则过期时间为0,返回200
		if(StringUtils.isNotBlank(userJson)){
			jedisClient.expire(userJson, -2);
			//如果查询到用户信息，则返回200
			return TaotaoResult.ok();
		}
		//2.通过键删除值
		return TaotaoResult.build(400, "您已经退出登陆啦，不要再做无谓的退出啦");
	}

}
