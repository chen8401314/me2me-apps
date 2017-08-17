package com.me2me.content.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.JsonObject;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dao.ContentMybatisDao;
import com.me2me.content.dao.LiveForContentJdbcDao;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentTags;
import com.me2me.content.model.ContentTagsDetails;
import com.me2me.content.service.ContentService;
import com.me2me.monitor.service.MonitorService;
import com.me2me.sms.service.JPushService;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/15
 * Time :16:55
 */
@Slf4j
public class AbstractWriteTag {

    @Autowired
    private ContentService contentService;

    @Autowired
    private UserService userService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private JPushService jPushService;
    
    @Autowired
    private LiveForContentJdbcDao liveForContentJdbcDao;
    
    @Autowired
    private ContentMybatisDao contentMybatisDao;

    public Response writeTag(WriteTagDto writeTagDto) {
        ContentTags contentTags = new ContentTags();
        contentTags.setTag(writeTagDto.getTag());
        contentService.createTag(contentTags);
        ContentTagsDetails contentTagsDetails = new ContentTagsDetails();
        contentTagsDetails.setTid(contentTags.getId());
        contentTagsDetails.setCid(writeTagDto.getCid());
        contentTagsDetails.setUid(writeTagDto.getUid());
        contentService.createContentTagsDetails(contentTagsDetails);
        Content content = contentService.getContentById(writeTagDto.getCid());
        //添加贴标签提醒
        if(content.getUid()!=writeTagDto.getUid()) {//自己不用收到提醒和推送
            if (content.getType() != Specification.ArticleType.LIVE.index) {
                log.info("ugc tag start");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType", Specification.PushMessageType.TAG.index);
                jsonObject.addProperty("type",Specification.PushObjectType.UGC.index);
                jsonObject.addProperty("cid",content.getId());
                String alias = String.valueOf(content.getUid());
                contentService.remind(content, writeTagDto.getUid(), Specification.UserNoticeType.TAG.index, writeTagDto.getTag());
                
                userService.pushWithExtra(alias, "你发布的内容收到了新感受", JPushUtils.packageExtra(jsonObject));
                log.info("ugc tag end");
            } else {
                log.info("live tag start");
//                Map<String,Object> topic = liveForContentJdbcDao.getTopicListByCid(content.getForwardCid());
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("messageType", Specification.PushMessageType.LIVE_TAG.index);
//                jsonObject.addProperty("type",Specification.PushObjectType.LIVE.index);
//                jsonObject.addProperty("topicId",content.getForwardCid());
//                jsonObject.addProperty("contentType", (Integer)topic.get("type"));
//                jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//此处是给王国创建者发的推送，所以直接设置核心圈
//                jsonObject.addProperty("fromInternalStatus", this.getInternalStatus(content.getForwardCid(), writeTagDto.getUid()));
//                String alias = String.valueOf(content.getUid());
//                contentService.remind(content, writeTagDto.getUid(), Specification.UserNoticeType.LIVE_TAG.index, writeTagDto.getTag());
//                if(this.checkTopicPush(content.getForwardCid(), content.getUid())){
//                	userService.pushWithExtra(alias, "你发布的内容收到了新感受", JPushUtils.packageExtra(jsonObject));
//                }
                log.info("live tag end");
            }
        }
            //打标签的时候文章热度+1
        content.setHotValue(content.getHotValue()+1);
        contentMybatisDao.updateContentById(content);
        //userService.push(content.getUid(),writeTagDto.getUid(),Specification.PushMessageType.TAG.index,content.getTitle());
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FEELING_TAG.index,0,writeTagDto.getUid()));
        return Response.success(ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.status,ResponseStatus.CONTENT_TAGS_LIKES_SUCCESS.message);
    }
    
    //判断核心圈身份
    private int getInternalStatus(long topicId, long uid) {
    	List<Long> ids = new ArrayList<Long>();
    	ids.add(topicId);
    	
    	List<Map<String,Object>> list = liveForContentJdbcDao.getTopicListByIds(ids);
    	if(null == list || list.size() == 0){
    		return 0;
    	}
    	Map<String,Object> topic = list.get(0);
    	
    	int internalStatus = 0;
        String coreCircle = (String)topic.get("core_circle");
        if(null != coreCircle){
        	JSONArray array = JSON.parseArray(coreCircle);
        	for (int i = 0; i < array.size(); i++) {
                if (array.getLong(i) == uid) {
                    internalStatus = Specification.SnsCircle.CORE.index;
                    break;
                }
            }
        }
        
//        if (internalStatus == 0) {
//            internalStatus = userService.getUserInternalStatus(uid, (Long)topic.get("uid"));
//        }

        return internalStatus;
    }
    
    private boolean checkTopicPush(long topicId, long uid){
    	Map<String,Object> tuc = liveForContentJdbcDao.getTopicUserConfig(topicId, uid);
    	if(null != tuc){
    		int pushType = (Integer)tuc.get("push_type");
    		if(pushType == 1){
    			return false;
    		}
    	}
    	return true;
    }
}
