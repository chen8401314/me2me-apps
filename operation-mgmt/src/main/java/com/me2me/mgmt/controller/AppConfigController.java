package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.me2me.cache.service.CacheService;
import com.me2me.mgmt.request.ConfigItem;
import com.me2me.mgmt.syslog.SystemControllerLog;

@Controller
@RequestMapping("/appconfig")
public class AppConfigController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfigController.class);
	
	@Autowired
	private CacheService cacheService;

	private List<ConfigItem> cacheConfigList = null;
	
	@PostConstruct
	public void init(){
		cacheConfigList = new ArrayList<ConfigItem>();
		ConfigItem ci = new ConfigItem("ad:url:key", "广告模式", ConfigItem.ConfigType.STRING);
		cacheConfigList.add(ci);
		ci = new ConfigItem("power:key", "管理员", ConfigItem.ConfigType.SET);
		cacheConfigList.add(ci);
	}
	
	@RequestMapping(value = "/cache/query")
	@SystemControllerLog(description = "缓存配置查询")
	public ModelAndView cacheConfigQuery(){
		List<ConfigItem> result = new ArrayList<ConfigItem>();
		ConfigItem item = null;
		for(ConfigItem c : cacheConfigList){
			item = new ConfigItem(c.getKey(), c.getDesc(), c.getType(), this.getCacheConfig(c.getKey(), c.getType()));
			result.add(item);
		}
		ModelAndView view = new ModelAndView("appconfig/cacheConfig");
		view.addObject("dataObj",result);
		
		return view;
	}
	
	private String getCacheConfig(String key, ConfigItem.ConfigType type){
		String result = "";
		if(StringUtils.isBlank(key) || null == type){
			return result;
		}
		if(ConfigItem.ConfigType.STRING == type){
			result = cacheService.get(key);
		}else if(ConfigItem.ConfigType.SET == type){
			Set<String> list = cacheService.smembers(key);
			if(null != list && list.size() > 0){
				for(String s : list){
					if(null != s && !"".equals(s)){
						result = result + ";" + s;
					}
				}
			}
			if(result.length() > 0){
				result = result.substring(1);
			}
		}else if(ConfigItem.ConfigType.MAP == type){
			Map<String,String> map = cacheService.hGetAll(key);
			if(null != null && map.size() > 0){
				for(Map.Entry<String, String> entry : map.entrySet()){
					result = result + ";" + entry.getKey() + "=" + entry.getValue();
				}
			}
			if(result.length() > 0){
				result = result.substring(1);
			}
		}//else 先这些，其他的在用到了再加
		
		return result;
	}
	
	@RequestMapping(value = "/cache/modify")
	@ResponseBody
	@SystemControllerLog(description = "缓存配置更新")
	public String modifyConfig(@RequestParam("k")String key, 
			@RequestParam("v")String value, 
			@RequestParam("t")int type){
		if(StringUtils.isBlank(key)){
			logger.warn("key不能为空");
			return "key不能为空";
		}
		ConfigItem.ConfigType configType = ConfigItem.ConfigType.getByType(type);
		if(null == configType){
			logger.warn("不支持的存储类型");
			return "不支持的存储类型";
		}
		if(StringUtils.isBlank(value) && ConfigItem.ConfigType.SET == configType){
			logger.warn("SET增加操作不能为空");
			return "SET增加操作不能为空";
		}
		
		if(ConfigItem.ConfigType.STRING == configType){
			cacheService.set(key, value);
		}else if(ConfigItem.ConfigType.SET == configType){
			cacheService.sadd(key, value);
		}
		
		return "0";
	}
}
