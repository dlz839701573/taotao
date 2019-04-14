package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserRegisterService;


@Service
public class UserRegisterServiceImpl implements UserRegisterService {
	@Autowired
	private TbUserMapper userMapper;
	
	@Override
	public TaotaoResult ChackData(String param, Integer type) {
		//1，注入mapper
		//2.根据参数动态生成查询条件
		TbUserExample example=new TbUserExample();
		Criteria criteria =example.createCriteria();
		if(type==1){//username
			//用户名不能为空	
			if(StringUtils.isEmpty(param)){
				return TaotaoResult.ok(false);
			}
			criteria.andUsernameEqualTo(param);
		}else if(type==2){//Phone
			criteria.andPhoneEqualTo(param);
		}else if(type==3){//Email
			criteria.andEmailEqualTo(param);
		}else{
			return TaotaoResult.build(400, "非法参数");
		}
		//3.调用mapper的查询方法查询数据
		List<TbUser> tbUser = userMapper.selectByExample(example);
		//4.如果查询到了数据，用false
		if(tbUser!=null&&tbUser.size()>0){
			return TaotaoResult.ok(false);
		}
		//5.如果没有查询到数据，用true
		return TaotaoResult.ok(true);
		//6.发布服务
	}

	
	
	@Override
	public TaotaoResult Register(TbUser user) {
		//1.注入mapper
		//2.数据效验
			//2.1验证username不能为空，并且唯一
			//2.2验证password不能为空，可以重复
			//2.3验证phone可以为空，（有数值）不可重复
			//2.4验证email可以为空，（有数值）不可重复
		//验证用户名和密码不能为空
		if(StringUtils.isEmpty(user.getUsername())){
			return TaotaoResult.build(400, "注册失败.用户名不能为空");
		}
		if(StringUtils.isEmpty(user.getPassword())){
			return TaotaoResult.build(400, "注册失败. 密码不能为空");
		}
		//验证用户名是否被注册
		TaotaoResult chackData = ChackData(user.getUsername(), 1);
		   //
		if(!(boolean)chackData.getData()){
			return TaotaoResult.build(400, "注册失败.用户名已经存在");
		}
		//验证:如果phone不为空，（有数值）不可重复(这句逻辑注意对错，容易曲解)
		if(StringUtils.isNotBlank(user.getPhone())){
			TaotaoResult chackPhone = ChackData(user.getPhone(), 2);
			if(!(boolean)chackPhone.getData()){
				return TaotaoResult.build(400, "注册失败.号码已经存在");
			}
		}
		//验证email可以为空，（有数值）不可重复
		if(StringUtils.isNotBlank(user.getEmail())){
			if((boolean)ChackData(user.getEmail(), 2).getData()){
			 return TaotaoResult.build(400, "注册失败.邮箱已经存在");
			}
		}
		//3.密码MD5加密
		String password=DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(password);
		//4.补全其他数据
		user.setUpdated(new Date());
		user.setCreated(new Date());
		//2.将数据插入到数据库()
		userMapper.insertSelective(user);
		//3.返回结果
		return TaotaoResult.ok();
		//4.发布服务
	}

}
