package com.me2me.live.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.content.dto.NewKingdom;
import com.me2me.content.service.ContentService;
import com.me2me.live.dao.LiveExtDao;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.CategoryKingdomsDto;
import com.me2me.live.dto.GetKingdomImageDTO;
import com.me2me.live.dto.KingdomImageListDTO;
import com.me2me.live.dto.KingdomImageMonthDTO;
import com.me2me.live.dto.TopicCategoryDto;
import com.me2me.live.dto.TopicCategoryDto.Category;
import com.me2me.live.mapper.TopicCategoryMapper;
import com.me2me.live.model.TopicCategory;
import com.me2me.live.model.TopicImage;
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
	@Autowired
	private TopicCategoryMapper categoryMapper;
	@Autowired
    private LiveMybatisDao liveMybatisDao;
	
	
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
	        if(data!=null){
		        NewKingdom coverKingdom= new NewKingdom();
		        coverKingdom.setUid((Long)data.get("uid"));
		        coverKingdom.setAvatar(Constant.QINIU_DOMAIN+"/"+data.get("avatar"));
		        coverKingdom.setCoverImage(Constant.QINIU_DOMAIN+"/"+data.get("live_image"));
		        coverKingdom.setNickName(data.get("nick_name").toString());
		        coverKingdom.setTitle(data.get("title").toString());
		        coverKingdom.setTopicId((Long)data.get("id"));
				dto.setCoverKingdom(coverKingdom);
	        }
		}
   
		List<Map<String,Object>> topicList =extDao.getCategoryKingdom(kcid,page,PAGE_SIZE);
		List<NewKingdom> kingdoms = contentService.buildFullNewKingdom(uid, topicList, 1);
		if(page==1 && dto.getCoverKingdom()==null && kingdoms.size()>0){
			dto.setCoverKingdom(kingdoms.get(0));
		}
		dto.setKingdoms(kingdoms);
		return Response.success(dto);
	}

	@Override
	public void addCategory(TopicCategory category) {
		categoryMapper.insertSelective(category);
	}

	@Override
	public void updateCategory(TopicCategory category) {
		categoryMapper.updateByPrimaryKeySelective(category);
	}

	@Override
	public TopicCategory getCategoryById(int id) {
		return categoryMapper.selectByPrimaryKey(id);
	}

	@Override
	public Response getKingdomImage(long uid,long topicId,long fid,String imageName,int type){
		GetKingdomImageDTO result = new GetKingdomImageDTO();
		
		int pageSize = 10;
		
		int totalCount = liveMybatisDao.getTotalTopicImageByTopic(topicId);
		result.setTotalCount(totalCount);
		
		List<TopicImage> currList = liveMybatisDao.getTopicImageByTopicIdAndFid(topicId, fid);
		if(null == currList){
			currList = new ArrayList<>();
		}
		//该fid前有多少个图片
		int beforeCount = liveMybatisDao.countTopicImageBefore(topicId, fid);
		
		TopicImage topicImage = null;
		GetKingdomImageDTO.ImageElement e = null;
		String image = null;
		if(type == 0){//向后拉取
			boolean flag = false;
			for(int i=0;i<currList.size();i++){
				beforeCount++;
				topicImage = currList.get(i);
				if(flag){//可以取数据了
					e = new GetKingdomImageDTO.ImageElement();
					e.setIndex(beforeCount);
					e.setFid(topicImage.getFid());
					image = topicImage.getImage();
					if(image.startsWith("http")){
						e.setImageName(image.substring(image.lastIndexOf("/")+1));
						e.setFragmentImage(image);
					}else{
						e.setImageName(image);
						e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+image);
					}
					e.setExtra(topicImage.getExtra());
					result.getImageDatas().add(e);
					if(result.getImageDatas().size() >= pageSize){
						break;
					}
				}else{
					if(imageName.equals(topicImage.getImage())){
						flag = true;
					}
				}
			}
			
			if(result.getImageDatas().size() < pageSize){//还需要补充
				List<TopicImage> list = liveMybatisDao.searchTopicImage(topicId, fid, 0, pageSize-result.getImageDatas().size());
				if(null != list && list.size() > 0){
					for(int i=0;i<list.size();i++){
						beforeCount++;
						topicImage = list.get(i);
						e = new GetKingdomImageDTO.ImageElement();
						e.setIndex(beforeCount);
						e.setFid(topicImage.getFid());
						image = topicImage.getImage();
						if(image.startsWith("http")){
							e.setImageName(image.substring(image.lastIndexOf("/")+1));
							e.setFragmentImage(image);
						}else{
							e.setImageName(image);
							e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+image);
						}
						e.setExtra(topicImage.getExtra());
						result.getImageDatas().add(e);
					}
				}
			}
		}else if(type == 1){//向前拉取
			beforeCount = beforeCount + currList.size() + 1;
			boolean flag = false;
			for(int i=currList.size()-1;i>=0;i--){
				topicImage = currList.get(i);
				beforeCount--;
				if(flag){
					e = new GetKingdomImageDTO.ImageElement();
					e.setIndex(beforeCount);
					e.setFid(topicImage.getFid());
					image = topicImage.getImage();
					if(image.startsWith("http")){
						e.setImageName(image.substring(image.lastIndexOf("/")+1));
						e.setFragmentImage(image);
					}else{
						e.setImageName(image);
						e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+image);
					}
					e.setExtra(topicImage.getExtra());
					result.getImageDatas().add(0, e);//往前插入
					if(result.getImageDatas().size() >= pageSize){
						break;
					}
				}else{
					if(imageName.equals(topicImage.getImage())){
						flag = true;
					}
				}
			}
			if(result.getImageDatas().size() < pageSize){//还需要补充
				List<TopicImage> list = liveMybatisDao.searchTopicImage(topicId, fid, 1, pageSize-result.getImageDatas().size());
				for(int i=0;i<list.size();i++){
					beforeCount--;
					topicImage = list.get(i);
					e = new GetKingdomImageDTO.ImageElement();
					e.setIndex(beforeCount);
					e.setFid(topicImage.getFid());
					image = topicImage.getImage();
					if(image.startsWith("http")){
						e.setImageName(image.substring(image.lastIndexOf("/")+1));
						e.setFragmentImage(image);
					}else{
						e.setImageName(image);
						e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+image);
					}
					e.setExtra(topicImage.getExtra());
					result.getImageDatas().add(0, e);
				}
			}
		}else if(type == 2){//前后都要
			int currStartCount = beforeCount+1;
			//前5
			int needBefore = 5;
			int needAfter = 5;
			for(int i=0;i<currList.size();i++){
				topicImage = currList.get(i);
				if(imageName.equals(topicImage.getImage())){
					needBefore = 5-result.getImageDatas().size();
					needAfter = 5-(currList.size()-i-1);
				}
				
				e = new GetKingdomImageDTO.ImageElement();
				e.setIndex(currStartCount);
				e.setFid(topicImage.getFid());
				image = topicImage.getImage();
				if(image.startsWith("http")){
					e.setImageName(image.substring(image.lastIndexOf("/")+1));
					e.setFragmentImage(image);
				}else{
					e.setImageName(image);
					e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+image);
				}
				e.setExtra(topicImage.getExtra());
				result.getImageDatas().add(e);
				currStartCount++;
			}
			
			if(needBefore > 0){//向前需要额外的
				List<TopicImage> list = liveMybatisDao.searchTopicImage(topicId, fid, 1, needBefore);
				for(int i=0;i<list.size();i++){
					topicImage = list.get(i);
					e = new GetKingdomImageDTO.ImageElement();
					e.setIndex(beforeCount);
					e.setFid(topicImage.getFid());
					image = topicImage.getImage();
					if(image.startsWith("http")){
						e.setImageName(image.substring(image.lastIndexOf("/")+1));
						e.setFragmentImage(image);
					}else{
						e.setImageName(image);
						e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+image);
					}
					e.setExtra(topicImage.getExtra());
					result.getImageDatas().add(0, e);
					beforeCount--;
				}
			}
			if(needAfter > 0){//向后需要额外的
				List<TopicImage> list = liveMybatisDao.searchTopicImage(topicId, fid, 0, needAfter);
				for(int i=0;i<list.size();i++){
					topicImage = list.get(i);
					e = new GetKingdomImageDTO.ImageElement();
					e.setIndex(currStartCount);
					e.setFid(topicImage.getFid());
					image = topicImage.getImage();
					if(image.startsWith("http")){
						e.setImageName(image.substring(image.lastIndexOf("/")+1));
						e.setFragmentImage(image);
					}else{
						e.setImageName(image);
						e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+image);
					}
					e.setExtra(topicImage.getExtra());
					result.getImageDatas().add(e);
					currStartCount++;
				}
			}
		}else{
			return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status, ResponseStatus.ILLEGAL_REQUEST.message);
		}
		
		return Response.success(result);
	}
	
	@Override
	public Response kingdomImageMonth(long uid, long topicId, long fid){
		KingdomImageMonthDTO result = new KingdomImageMonthDTO();
		
		int monthCount = 0;
		String showMonth = null;
		List<Map<String, Object>> list = extDao.getKingdomImageMonth(topicId);
		if(null != list && list.size() > 0){
			monthCount = list.size();
			Map<String, Object> m = null;
			KingdomImageMonthDTO.MonthElement e = null;
			for(int i=0;i<list.size();i++){
				m = list.get(i);
				if(i == 0){
					showMonth = (String)m.get("mm");
				}else{
					long minFid = (Long)m.get("minfid");
					long maxFid = (Long)m.get("maxfid");
					if(fid>=minFid && fid<=maxFid){
						showMonth = (String)m.get("mm");
					}
				}
				
				e = new KingdomImageMonthDTO.MonthElement();
				e.setMonth((String)m.get("mm"));
				e.setImageCount(((Long)m.get("cc")).intValue());
				result.getMonthData().add(e);
			}
		}
		
		result.setMonthCount(monthCount);
		result.setShowMonth(showMonth);
		
		return Response.success(result);
	}
	
	@Override
	public Response kingdomImageList(long uid, long topicId, String month){
		KingdomImageListDTO result = new KingdomImageListDTO();
		
		List<Map<String, Object>> list = extDao.getKingdomImageList(topicId, month);
		if(null != list && list.size() > 0){
			KingdomImageListDTO.ImageElement e = null;
			for(Map<String, Object> m : list){
				e = new KingdomImageListDTO.ImageElement();
				e.setFid((Long)m.get("fid"));
				e.setImageName((String)m.get("image"));
				e.setFragmentImage(Constant.QINIU_DOMAIN+"/"+(String)m.get("image"));
				e.setExtra((String)m.get("extra"));
				result.getImageDatas().add(e);
			}
		}
		
		return Response.success(result);
	}
}
