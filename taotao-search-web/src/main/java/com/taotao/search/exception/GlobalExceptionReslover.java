package com.taotao.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
/**
 * 全局异常处理
 * @author Administrator
 *
 */

public class GlobalExceptionReslover implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//写入日志，这里直接打印出来
		System.err.println(ex.getMessage());
		//通知开发人员
		System.out.println("发短信给开发人员");
		//返回给用户相应的异常原因
		ModelAndView view=new ModelAndView();
		view.setViewName("/error/exception");
		view.addObject("message", "你可能不相信，但是这确实是真的，我跑路了");
		return view;
		
		//在xml文件中配置
	}

}
