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

import com.me2me.activity.model.AppLightboxSource;
import com.me2me.activity.model.AppUiControl;
import com.me2me.activity.service.ActivityService;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.DateUtil;
import com.me2me.common.web.Response;
import com.me2me.mgmt.request.AppUIItem;
import com.me2me.mgmt.request.AppUIQueryDTO;
import com.me2me.mgmt.request.AppVersionQueryDTO;
import com.me2me.mgmt.request.ConfigItem;
import com.me2me.mgmt.request.LightBoxItem;
import com.me2me.mgmt.request.LightBoxQueryDTO;
import com.me2me.mgmt.request.VersionChannelAddrQueryDTO;
import com.me2me.mgmt.syslog.SystemControllerLog;
import com.me2me.user.dto.ShowVersionControlDto;
import com.me2me.user.dto.VersionControlDto;
import com.me2me.user.model.AppConfig;
import com.me2me.user.model.SystemConfig;
import com.me2me.user.model.VersionChannelDownload;
import com.me2me.user.service.UserService;

@Controller
@RequestMapping("/appconfig")
public class AppConfigController {
	
	private static final Logger logger = LoggerFactory.getLogger(AppConfigController.class);
	
	@Autowired
	private CacheService cacheService;
	@Autowired
    private UserService userService;
	@Autowired
	private ActivityService activityService;

	private List<ConfigItem> cacheConfigList = null;
	
	private List<ConfigItem> dbConfigList = null;
	
	@PostConstruct
	public void init(){
		cacheConfigList = new ArrayList<ConfigItem>();
		ConfigItem ci = new ConfigItem("ad:url:key", "广告模式", ConfigItem.ConfigType.STRING);
		cacheConfigList.add(ci);
		ci = new ConfigItem("power:key", "管理员", ConfigItem.ConfigType.SET);
		cacheConfigList.add(ci);
		ci = new ConfigItem("NORMAL_CREATE_VOTE_COUNT", "普通用户每天发起投票次数限制", ConfigItem.ConfigType.STRING);
		cacheConfigList.add(ci);
		ci = new ConfigItem("V_CREATE_VOTE_COUNT", "大V用户每天发起投票次数限制", ConfigItem.ConfigType.STRING);
		cacheConfigList.add(ci);
		
		dbConfigList = new ArrayList<ConfigItem>();
		ci = new ConfigItem(ConfigItem.DBConfig.DEFAULT_FOLLOW.getKey(), ConfigItem.DBConfig.DEFAULT_FOLLOW.getDesc(), ConfigItem.ConfigType.DB);
		dbConfigList.add(ci);
		ci = new ConfigItem(ConfigItem.DBConfig.DEFAULT_SUBSCRIBE.getKey(), ConfigItem.DBConfig.DEFAULT_SUBSCRIBE.getDesc(), ConfigItem.ConfigType.DB);
		dbConfigList.add(ci);
		ci = new ConfigItem(ConfigItem.DBConfig.READ_COUNT_START.getKey(), ConfigItem.DBConfig.READ_COUNT_START.getDesc(), ConfigItem.ConfigType.DB);
		dbConfigList.add(ci);
		ci = new ConfigItem(ConfigItem.DBConfig.READ_COUNT_END.getKey(), ConfigItem.DBConfig.READ_COUNT_END.getDesc(), ConfigItem.ConfigType.DB);
		dbConfigList.add(ci);
	}
	
	@RequestMapping(value = "/cache/query")
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
		}else if(ConfigItem.DBConfig.DEFAULT_SUBSCRIBE.getKey().equals(key)){
			return sconfig.getDefaultSubscribe();
		}
		
		return "";
	}
	
	@RequestMapping(value = "/newconfig/query")
	public ModelAndView newConfigQuery(){
		List<ConfigItem> result = new ArrayList<ConfigItem>();
		
		List<AppConfig> list = userService.getAllAppConfig();
		if(null != list && list.size() > 0){
			ConfigItem item = null;
			for(AppConfig config : list){
				item = new ConfigItem(config.getConfigKey(),config.getName(),ConfigItem.ConfigType.DB,config.getConfigValue());
				result.add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("appconfig/dbConfig");
		view.addObject("dataObj",result);
		
		return view;
	}
	
	@RequestMapping(value = "/newconfig/modify")
	@ResponseBody
	@SystemControllerLog(description = "新APP配置更新")
	public String modifyNewConfig(@RequestParam("k")String key, 
			@RequestParam("v")String value){
		if(StringUtils.isBlank(key)){
			logger.warn("key不能为空");
			return "key不能为空";
		}
		
		userService.saveAppConfig(key, value, "");
		
		return "0";
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
	
	@RequestMapping(value = "/dbconfig/modify")
	@ResponseBody
	@SystemControllerLog(description = "数据库配置更新")
	public String modifyDBConfig(@RequestParam("k")String key, 
			@RequestParam("v")String value,
			@RequestParam("i")long cid){
		if(StringUtils.isBlank(key)){
			logger.warn("key不能为空");
			return "key不能为空";
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
		}else if(ConfigItem.DBConfig.DEFAULT_SUBSCRIBE.getKey().equals(key)){
			config.setDefaultSubscribe(value);
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
	
	@RequestMapping(value = "/ui/query")
	public ModelAndView queryAppUI(AppUIQueryDTO dto){
		ModelAndView view = new ModelAndView("appconfig/appuiList");
		
		List<AppUiControl> list = activityService.getAppUiControlList(dto.getSearchTime());
		if(null != list && list.size() > 0){
			List<AppUIItem> result = new ArrayList<AppUIItem>();
			AppUIItem item = null;
			for(AppUiControl ui : list){
				item = new AppUIItem();
				item.setId(ui.getId());
				item.setSourceCode(ui.getSourceCode());
				item.setDescription(ui.getDescription());
				item.setStartTime(DateUtil.date2string(ui.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
				item.setEndTime(DateUtil.date2string(ui.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
				item.setStatus(ui.getStatus());
				result.add(item);
			}
			dto.setResult(result);
		}

		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value = "/ui/find/{id}")
	public ModelAndView getUI(@PathVariable long id){
		ModelAndView view = new ModelAndView("appconfig/appuiEdit");
		AppUiControl ui = activityService.getAppUiControlById(id);
		AppUIItem item = new AppUIItem();
		item.setId(ui.getId());
		item.setSourceCode(ui.getSourceCode());
		item.setDescription(ui.getDescription());
		item.setStartTime(DateUtil.date2string(ui.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		item.setEndTime(DateUtil.date2string(ui.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		item.setStatus(ui.getStatus());
		
		view.addObject("dataObj",item);
		return view;
	}
	
	@RequestMapping(value = "/ui/update")
	@SystemControllerLog(description = "更新APP主题")
	public ModelAndView updateAppUI(AppUIItem item){
		AppUiControl ui = activityService.getAppUiControlById(item.getId());
		ui.setSourceCode(item.getSourceCode());
		ui.setDescription(item.getDescription());
		try{
			ui.setStartTime(DateUtil.string2date(item.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			ui.setEndTime(DateUtil.string2date(item.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		}catch(Exception e){
			logger.error("时间转换失败", e);
			ModelAndView view = new ModelAndView("appconfig/appuiEdit");
			view.addObject("errMsg","时间格式错误");
			return view;
		}
		ui.setStatus(item.getStatus());
		activityService.updateAppUiControl(ui);
		ModelAndView view = new ModelAndView("redirect:/appconfig/ui/query");
		return view;
	}
	
	@RequestMapping(value = "/ui/create")
	@SystemControllerLog(description = "新增APP主题")
	public ModelAndView createAppUI(AppUIItem item){
		AppUiControl ui = new AppUiControl();
		ui.setSourceCode(item.getSourceCode());
		ui.setDescription(item.getDescription());
		try{
			ui.setStartTime(DateUtil.string2date(item.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			ui.setEndTime(DateUtil.string2date(item.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		}catch(Exception e){
			logger.error("时间转换失败", e);
			ModelAndView view = new ModelAndView("appconfig/appuiNew");
			view.addObject("errMsg","时间格式错误");
			return view;
		}
		ui.setStatus(item.getStatus());
		activityService.createAppUiControl(ui);
		ModelAndView view = new ModelAndView("redirect:/appconfig/ui/query");
		return view;
	}
	
	@RequestMapping(value = "/lightbox/query")
	public ModelAndView queryLightBox(LightBoxQueryDTO dto){
		ModelAndView view = new ModelAndView("appconfig/lightboxList");
		
		List<AppLightboxSource> list = activityService.getAppLightboxSourceList(dto.getSearchTime());
		if(null != list && list.size() > 0){
			List<LightBoxItem> result = new ArrayList<LightBoxItem>();
			LightBoxItem item = null;
			for(AppLightboxSource l : list){
				item = new LightBoxItem();
				item.setId(l.getId());
				item.setImage(l.getImage());
				item.setMainText(l.getMainText());
				item.setSecondaryText(l.getSecondaryText());
				item.setMainTone(l.getMainTone());
				item.setLinkUrl(l.getLinkUrl());
				item.setStartTime(DateUtil.date2string(l.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
				item.setEndTime(DateUtil.date2string(l.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
				item.setStatus(l.getStatus());
				result.add(item);
			}
			dto.setResult(result);
		}

		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value = "/lightbox/find/{id}")
	public ModelAndView getLightBox(@PathVariable long id){
		ModelAndView view = new ModelAndView("appconfig/lightboxEdit");
		AppLightboxSource l = activityService.getAppLightboxSourceById(id);
		LightBoxItem item = new LightBoxItem();
		item.setId(l.getId());
		item.setImage(l.getImage());
		item.setMainText(l.getMainText());
		item.setSecondaryText(l.getSecondaryText());
		item.setMainTone(l.getMainTone());
		item.setLinkUrl(l.getLinkUrl());
		item.setStartTime(DateUtil.date2string(l.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		item.setEndTime(DateUtil.date2string(l.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		item.setStatus(l.getStatus());
		
		view.addObject("dataObj",item);
		return view;
	}
	
	@RequestMapping(value = "/lightbox/update")
	@SystemControllerLog(description = "更新APP灯箱页")
	public ModelAndView updateLightBox(LightBoxItem item){
		AppLightboxSource l = activityService.getAppLightboxSourceById(item.getId());
		l.setImage(item.getImage());
		l.setMainText(item.getMainText());
		l.setSecondaryText(item.getSecondaryText());
		l.setMainTone(item.getMainTone());
		l.setLinkUrl(item.getLinkUrl());
		try{
			l.setStartTime(DateUtil.string2date(item.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			l.setEndTime(DateUtil.string2date(item.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		}catch(Exception e){
			logger.error("时间转换失败", e);
			ModelAndView view = new ModelAndView("appconfig/lightboxEdit");
			view.addObject("errMsg","时间格式错误");
			return view;
		}
		l.setStatus(item.getStatus());
		activityService.updateAppLightboxSource(l);

		ModelAndView view = new ModelAndView("redirect:/appconfig/lightbox/query");
		return view;
	}
	
	@RequestMapping(value = "/lightbox/create")
	@SystemControllerLog(description = "新增APP灯箱页")
	public ModelAndView createLightBox(LightBoxItem item){
		AppLightboxSource l = new AppLightboxSource();
		l.setImage(item.getImage());
		l.setMainText(item.getMainText());
		l.setSecondaryText(item.getSecondaryText());
		l.setMainTone(item.getMainTone());
		l.setLinkUrl(item.getLinkUrl());
		try{
			l.setStartTime(DateUtil.string2date(item.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			l.setEndTime(DateUtil.string2date(item.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
		}catch(Exception e){
			logger.error("时间转换失败", e);
			ModelAndView view = new ModelAndView("appconfig/lightboxNew");
			view.addObject("errMsg","时间格式错误");
			return view;
		}
		l.setStatus(item.getStatus());
		activityService.createAppLightboxSource(l);
		
		ModelAndView view = new ModelAndView("redirect:/appconfig/lightbox/query");
		return view;
	}
	
	@RequestMapping(value = "/version/channel/query")
	public ModelAndView versionChannelAddrQuery(VersionChannelAddrQueryDTO dto){
		dto.getResult().clear();
		List<VersionChannelDownload> list = userService.queryVersionChannelDownloads(dto.getChannel());
		if(null != list && list.size() > 0){
			VersionChannelAddrQueryDTO.Item item = null;
			for(VersionChannelDownload vcd : list){
				item = new VersionChannelAddrQueryDTO.Item();
				item.setChannel(vcd.getChannel());
				item.setId(vcd.getId());
				item.setType(vcd.getType());
				if(vcd.getType() == 0){
					item.setVersionAddr("第三方拉取");
				}else{
					item.setVersionAddr(vcd.getVersionAddr());
				}
				dto.getResult().add(item);
			}
		}
		
		ModelAndView view = new ModelAndView("appconfig/versionChannelAddrList");
		view.addObject("dataObj",dto);
		return view;
	}
	
	@RequestMapping(value = "/version/channel/create")
	@SystemControllerLog(description = "创建版本渠道下载地址")
	public ModelAndView createVersionChannelAddr(VersionChannelAddrQueryDTO.Item item){
		if(item.getType() != 0 && StringUtils.isBlank(item.getVersionAddr())){
			ModelAndView view = new ModelAndView("appconfig/versionChannelAddrNew");
			view.addObject("errMsg","本地下载类型，地址必填");
			return view;
		}
		
		VersionChannelDownload vcd = userService.getVersionChannelDownloadByChannel(item.getChannel());
		if(null != vcd){
			ModelAndView view = new ModelAndView("appconfig/versionChannelAddrNew");
			view.addObject("errMsg","渠道不能重复");
			return view;
		}
		vcd = new VersionChannelDownload();
		vcd.setChannel(item.getChannel());
		vcd.setType(item.getType());
		if(item.getType() == 0){
			vcd.setVersionAddr("");
		}else{
			vcd.setVersionAddr(item.getVersionAddr());
		}
		userService.saveVersionChannelDownload(vcd);
		
		ModelAndView view = new ModelAndView("redirect:/appconfig/version/channel/query");
		return view;
	}
	
	@RequestMapping(value = "/version/channel/find/{id}")
	public ModelAndView findVersionChannelAddr(@PathVariable long id){
		VersionChannelDownload vcd = userService.getVersionChannelDownloadById(id);
		
		VersionChannelAddrQueryDTO.Item item = new VersionChannelAddrQueryDTO.Item();
		if(null != vcd){
			item.setId(vcd.getId());
			item.setChannel(vcd.getChannel());
			item.setType(vcd.getType());
			item.setVersionAddr(vcd.getVersionAddr());
		}
		
		ModelAndView view = new ModelAndView("appconfig/versionChannelAddrEdit");
		view.addObject("dataObj",item);
		return view;
	}
	
	@RequestMapping(value = "/version/channel/editSave")
	@SystemControllerLog(description = "更新版本渠道下载地址")
	public ModelAndView saveEditVersionChannelAddr(VersionChannelAddrQueryDTO.Item item){
		VersionChannelDownload vcd = userService.getVersionChannelDownloadById(item.getId());
		
		//校验渠道唯一
		if(!vcd.getChannel().equals(item.getChannel())){
			VersionChannelDownload checkVcd = userService.getVersionChannelDownloadByChannel(item.getChannel());
			if(null != checkVcd){
				ModelAndView view = new ModelAndView("appconfig/versionChannelAddrEdit");
				view.addObject("errMsg","渠道不能重复");
				view.addObject("dataObj",item);
				return view;
			}
		}
		
		vcd.setChannel(item.getChannel());
		vcd.setType(item.getType());
		if(item.getType() == 0){
			vcd.setVersionAddr("");
		}else{
			vcd.setVersionAddr(item.getVersionAddr());
		}
		userService.updateVersionChannelDownload(vcd);
		
		ModelAndView view = new ModelAndView("redirect:/appconfig/version/channel/query");
		return view;
	}
	
	@RequestMapping(value = "/version/channel/del/{id}")
	@SystemControllerLog(description = "删除版本渠道下载地址")
	public ModelAndView deleteVersionChannelAddr(@PathVariable long id){
		userService.deleteVersionChannelDownload(id);
		ModelAndView view = new ModelAndView("redirect:/appconfig/version/channel/query");
		return view;
	}
}
