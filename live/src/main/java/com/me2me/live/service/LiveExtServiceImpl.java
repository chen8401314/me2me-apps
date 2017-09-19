package com.me2me.live.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.live.dao.LiveExtDao;
import com.me2me.live.dto.TopicCategoryDto;
import com.me2me.live.dto.TopicCategoryDto.Category;
import com.me2me.live.model.TopicCategory;

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
	public Response kingdomByCategory(int uid, int kcid, int page) {
		
		
		return null;
	}

}
