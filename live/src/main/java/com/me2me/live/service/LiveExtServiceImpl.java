package com.me2me.live.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.content.dto.NewKingdom;
import com.me2me.content.service.ContentService;
import com.me2me.live.dao.LiveExtDao;
import com.me2me.live.dto.CategoryKingdomsDto;
import com.me2me.live.dto.TopicCategoryDto;
import com.me2me.live.dto.TopicCategoryDto.Category;
import com.me2me.live.model.TopicCategory;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * liveService 扩展服务。
 * @author zhangjiwei
 * @date Sep 19, 2017
 */
@Slf4j
@Service
public class LiveExtServiceImpl implements LiveExtService {
	@Autowired
	private LiveExtDao extDao;
	private static final int PAGE_SIZE= 20;
	@Autowired
	private ContentService contentService;
	@Autowired
	private UserService userService;
	@Override
	public Response category() {
		List<TopicCategory> cats = extDao.getAllCategory();
		TopicCategoryDto dto = new TopicCategoryDto();
		for(TopicCategory tc:cats){
			Category ct = new Category();
			ct.setKcIcon(Constant.QINIU_DOMAIN+"/"+tc.getIcon());
			ct.setKcid(tc.getId());
			ct.setKcImage(Constant.QINIU_DOMAIN+"/"+tc.getCoverImg());
			ct.setKcName(tc.getName());
			dto.getCategories().add(ct);
		}
		return Response.success(dto);
	}

	@Override
	public Response kingdomByCategory(long uid, int kcid, int page) {
		CategoryKingdomsDto dto = new CategoryKingdomsDto();
		if(page==1){
			TopicCategory tc=  extDao.getCategoryById(kcid);
			Category ct= new Category();
			ct.setKcIcon(tc.getIcon());
			ct.setKcid(tc.getId());
			ct.setKcImage(tc.getCoverImg());
			ct.setKcName(tc.getName());
			dto.setCategory(ct);
			String v = userService.getAppConfigByKey("KINGDOM_OUT_MINUTE");
	        int limitMinute = 3;
	        if(!StringUtils.isEmpty(v)){
	        	limitMinute = Integer.valueOf(v).intValue();
	        }
	        Map<String,Object> data= extDao.getCategoryCoverKingdom(kcid,limitMinute);
	        NewKingdom coverKingdom= new NewKingdom();
	        coverKingdom.setUid((Long)data.get("uid"));
	        coverKingdom.setAvatar(Constant.QINIU_DOMAIN+"/"+data.get("live_img"));
	        coverKingdom.setNickName(data.get("nick_name").toString());
	        coverKingdom.setTitle(data.get("title").toString());
	        coverKingdom.setTopicId((Long)data.get("id"));
			dto.setCoverKingdom(coverKingdom);
		}
   
		List<Map<String,Object>> topicList =extDao.getCategoryKingdom(kcid,page,PAGE_SIZE);
		List<NewKingdom> kingdoms = contentService.buildFullNewKingdom(uid, topicList);
		dto.setKingdoms(kingdoms);
		return Response.success(dto);
	}

}
