package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.me2me.common.Constant;
import com.me2me.common.security.SecurityUtils;
import com.me2me.io.service.FileTransferService;

@Controller
@RequestMapping("/upload")
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	private FileTransferService fileTransferService;
	
	@RequestMapping(value = "/image", method = RequestMethod.POST)
	@ResponseBody
	public String image(HttpServletRequest request, @RequestParam("filedata") MultipartFile file) throws Exception {
		int maxattachsize = 20971520;// 最大上传大小，默认是20M
		
		String error = "";
		String url = "";
		if(file.getSize() > maxattachsize){
			error = "上传图片不能超过20M";
		}else{
			try{
				String imgName = SecurityUtils.md5(request.getSession().getId()+System.currentTimeMillis(), "1");
				fileTransferService.upload(file.getBytes(), imgName);
				url = Constant.QINIU_DOMAIN + "/" + imgName;
			}catch(Exception e){
				logger.error("上传失败", e);
				error = "上传失败";
			}
		}
		// 取得原文件名
		String originName = file.getOriginalFilename();

		String json = String.format("{'err':'%s',msg:{'url':'%s','localname':'%s','id':'1'}}", error, url, originName);
		return json;
	}
}
