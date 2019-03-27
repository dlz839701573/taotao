package com.taotao.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.util.JsonUtils;
import com.taotao.managerweb.util.FastDFSClient;

@Controller
public class PictureController {
	
	@Value("${TAOTAO_IMANE_SERVER_URL}")
	private String TAOTAO_IMAGE_SERVER_URL;
	
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String uploadPic(MultipartFile uploadFile){
		try {
			//1.创建一个fastdfsclient对象
			FastDFSClient client = new FastDFSClient("classpath:resource/client.conf");
			//2.获取元文件的字节数组
			byte[] bytes = uploadFile.getBytes();
			//3.获取元文件的扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			//不带点的扩展名
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			
			//4.调用方法上传图片
				//返回的字符串：group1/M00/00/01/wKgZhVjnAd6AKj_RAAvqH_kipG8211.jpg
			String uploadFile2 = client.uploadFile(bytes, extName);
			//5.设置上传成功后的图片的路径和错误信息
			Map map = new HashMap<>();
			map.put("error", 0);
			map.put("url", TAOTAO_IMAGE_SERVER_URL+uploadFile2);
			//6.返回
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			//不成功的情况
			Map map = new HashMap<>();
			map.put("error", 0);
			map.put("message","上传图片失败");
			return JsonUtils.objectToJson(map);
		}
	}
}