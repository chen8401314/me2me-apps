package com.me2me.web.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

public class DevInterceptor implements HandlerInterceptor{
	private static Logger logger = LoggerFactory.getLogger(DevInterceptor.class);
	private Properties props;
	private boolean isDevMode = false;
	@PostConstruct
	public void init(){
		props =new Properties();
		try {
			props.load(DevInterceptor.class.getResourceAsStream("/dev_settings.properties"));
			isDevMode= props.getProperty("devMode", "").equals("dev")||props.getProperty("devMode", "").equals("test");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 获取输入流的内容。
	 * @date 2011-11-7 
	 * @param is 字符流
	 * @param charset 输入流的字符编码，如果不明确charset请使用{@link #getContent}
	 * @return InputStream 的字符串内容
	 */
	public static String toString(InputStream is,String charset){
		String pageString = null;
		try {
			InputStreamReader isr=new InputStreamReader(is,charset);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			pageString = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
				try {
					if (is!=null) is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return pageString;
	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(isDevMode){
			String requestUri = request.getRequestURI();
			if(!StringUtils.isEmpty(requestUri)){
				String isTest= request.getParameter("_isTest");
				boolean isTestMode = "1".equals(isTest);
				String targetFile = requestUri.substring(1).replaceAll("/$", "").replace("/", ".");
				boolean isValid =props.getProperty(targetFile, "").equals("true");
				if(isValid || isTestMode){
					targetFile+=".json";
					InputStream inputStream=null;
					response.setContentType("application/json; charset=utf-8");
					Writer writer= response.getWriter();
					try{
						inputStream = DevInterceptor.class.getResourceAsStream("/devJson/"+targetFile);
						String json= IOUtils.toString(inputStream,"UTF-8");
						Object obj = JSON.parse(json);
						writer.write(JSON.toJSONString(obj,true));
						return false;
					}catch(JSONException e){
						writer.write("json文件存在语法错误["+e.getMessage()+"]，请检查："+targetFile);
						return false;
					}catch(Exception e){
						logger.error("开发模式 找不到文件:"+targetFile+",请检查classpath:/devJson目录是否有此文件");
					}finally {
						if(inputStream!=null){
							inputStream.close();
						}
						writer.flush();
						writer.close();
					}
				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
