package com.me2me.mgmt.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.cache.service.CacheService;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.AppVersionQueryDTO;
import com.me2me.mgmt.request.ConfigItem;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.user.dto.ShowVersionControlDto;
import com.me2me.user.dto.VersionControlDto;
import com.me2me.user.model.SystemConfig;
import com.me2me.user.service.UserService;

@Controller
@RequestMapping("/appconfig")
public class AppConfigController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfigController.class);
	
	@Autowired
	private CacheService cacheService;
	@Autowired
    private UserService userService;

	private List<ConfigItem> cacheConfigList = null;
	
	private List<ConfigItem> dbConfigList = null;
	
	@PostConstruct
	public void init(){
		cacheConfigList = new ArrayList<ConfigItem>();
		ConfigItem ci = new ConfigItem("ad:url:key", "广告模式", ConfigItem.ConfigType.STRING);
		cacheConfigList.add(ci);
		ci = new ConfigItem("power:key", "管理员", ConfigItem.ConfigType.SET);
		cacheConfigList.add(ci);
		
		dbConfigList = new ArrayList<ConfigItem>();
		ci = new ConfigItem(ConfigItem.DBConfig.DEFAULT_FOLLOW.getKey(), ConfigItem.DBConfig.DEFAULT_FOLLOW.getDesc(), ConfigItem.ConfigType.DB);
		dbConfigList.add(ci);
		ci = new ConfigItem(ConfigItem.DBConfig.READ_COUNT_START.getKey(), ConfigItem.DBConfig.READ_COUNT_START.getDesc(), ConfigItem.ConfigType.DB);
		dbConfigList.add(ci);
		ci = new ConfigItem(ConfigItem.DBConfig.READ_COUNT_END.getKey(), ConfigItem.DBConfig.READ_COUNT_END.getDesc(), ConfigItem.ConfigType.DB);
		dbConfigList.add(ci);
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
	
	@RequestMapping(value = "/dbconfig/query")
	@SystemControllerLog(description = "数据库配置查询")
	public ModelAndView dbConfigQuery(){
		List<ConfigItem> result = new ArrayList<ConfigItem>();
		
		SystemConfig sconfig = userService.getSystemConfig();
		ConfigItem item = null;
		for(ConfigItem c : dbConfigList){
			item = new ConfigItem(c.getKey(), c.getDesc(), c.getType());
			if(null != sconfig){
				item.setValue(this.getDbConfigValue(c.getKey(), sconfig));
			}
			result.add(item);
		}
		
		ModelAndView view = new ModelAndView("appconfig/dbConfig");
		view.addObject("dataObj",result);
		long configId = 0;
		if(null != sconfig){
			configId = sconfig.getId();
		}
		view.addObject("configId", configId);
		
		return view;
	}
	
	private String getDbConfigValue(String key, SystemConfig sconfig){
		if(ConfigItem.DBConfig.DEFAULT_FOLLOW.getKey().equals(key)){
			return sconfig.getDefaultFollow();
		}else if(ConfigItem.DBConfig.READ_COUNT_START.getKey().equals(key)){
			return sconfig.getReadCountStart().toString();
		}else if(ConfigItem.DBConfig.READ_COUNT_END.getKey().equals(key)){
			return sconfig.getReadCountEnd().toString();
		}
		
		return "";
	}
	
	@RequestMapping(value = "/cache/modify")
	@ResponseBody
	@SystemControllerLog(description = "缓存配置更新")
	public String modifyConfig(@RequestParam("k")String key, 
			@RequestParam("v")String value, 
			@RequestParam("t")int type){
		if(StringUtils.isBlank(key) || StringUtils.isBlank(value)){
			logger.warn("key和value不能为空");
			return "key和value不能为空";
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
	
	@RequestMapping(value = "/dbconfig/modify")
	@ResponseBody
	@SystemControllerLog(description = "数据库配置更新")
	public String modifyDBConfig(@RequestParam("k")String key, 
			@RequestParam("v")String value,
			@RequestParam("i")long cid){
		if(StringUtils.isBlank(key) || StringUtils.isBlank(value)){
			logger.warn("key和value不能为空");
			return "key和value不能为空";
		}
		
		if(cid <= 0){
			logger.warn("数据库中不存在配置，请先初始化数据库系统配置表");
			return "数据库中不存在配置，请先初始化数据库系统配置表";
		}
		
		SystemConfig config = new SystemConfig();
		config.setId(cid);
		if(ConfigItem.DBConfig.DEFAULT_FOLLOW.getKey().equals(key)){
			config.setDefaultFollow(value);
		}else if(ConfigItem.DBConfig.READ_COUNT_START.getKey().equals(key)){
			Integer v = Integer.valueOf(value);
			config.setReadCountStart(v);
		}else if(ConfigItem.DBConfig.READ_COUNT_END.getKey().equals(key)){
			Integer v = Integer.valueOf(value);
			config.setReadCountEnd(v);
		}else{
			logger.warn("不支持的key");
			return "不支持的key";
		}
		userService.updateSystemConfig(config);
		
		return "0";
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/version/query")
	@SystemControllerLog(description = "APP版本查询")
	public ModelAndView versionQuery(AppVersionQueryDTO dto){
		Response resp = userService.getVersionList(dto.getVersion(), dto.getPlatform());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			ShowVersionControlDto data = (ShowVersionControlDto)resp.getData();
			if(null != data.getResult() && data.getResult().size() > 0){
				for(VersionControlDto v : data.getResult()){
					if(StringUtils.isNotBlank(v.getUpdateDescription())){
						v.setUpdateDescription(v.getUpdateDescription().replaceAll("\n", "<br/>"));
					}
				}
			}
			dto.setData(data);
		}
		ModelAndView view = new ModelAndView("appconfig/versionList");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/version/find/{vid}")
	public ModelAndView getVersion(@PathVariable long vid){
		Response resp = userService.getVersionById(vid);
		ModelAndView view = new ModelAndView("appconfig/versionEdit");
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			VersionControlDto dto = (VersionControlDto) resp.getData();
			view.addObject("dataObj",dto);
		}
		
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/version/create")
	@SystemControllerLog(description = "创建APP版本")
	public ModelAndView createVersion(VersionControlDto dto){
		ModelAndView view = null;
		if(StringUtils.isBlank(dto.getVersion()) 
				|| StringUtils.isBlank(dto.getUpdateDescription())
				|| StringUtils.isBlank(dto.getUpdateUrl())){
			view = new ModelAndView("appconfig/versionNew");
			view.addObject("errMsg","请求参数不能为空");
			return view;
		}
		
		Response resp = userService.getVersion(dto.getVersion(), dto.getPlatform());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			view = new ModelAndView("appconfig/versionNew");
			view.addObject("errMsg",(dto.getPlatform()==1?"安卓":"IOS") + "已经存在该版本["+dto.getVersion()+"]了");
			return view;
		}
		
		userService.saveOrUpdateVersion(dto);
		
		view = new ModelAndView("redirect:/appconfig/version/query");
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/version/edit/save")
	@SystemControllerLog(description = "更新APP版本")
	public ModelAndView saveEditVersion(VersionControlDto dto){
		ModelAndView view = null;
		if(dto.getId() <= 0 ||StringUtils.isBlank(dto.getVersion()) 
				|| StringUtils.isBlank(dto.getUpdateDescription())
				|| StringUtils.isBlank(dto.getUpdateUrl())){
			view = new ModelAndView("appconfig/versionEdit");
			view.addObject("errMsg","请求参数不能为空");
			view.addObject("dataObj",dto);
			return view;
		}
		
		Response resp = userService.getVersion(dto.getVersion(), dto.getPlatform());
		if(null != resp && resp.getCode() == 200 && null != resp.getData()){
			VersionControlDto vcdto = (VersionControlDto) resp.getData();
			if(vcdto.getId() != dto.getId()){
				view = new ModelAndView("appconfig/versionEdit");
				view.addObject("errMsg",(dto.getPlatform()==1?"安卓":"IOS") + "已经存在该版本["+dto.getVersion()+"]了");
				view.addObject("dataObj",dto);
				return view;
			}
		}
		
		userService.saveOrUpdateVersion(dto);
		
		view = new ModelAndView("redirect:/appconfig/version/query");
		return view;
	}
}
