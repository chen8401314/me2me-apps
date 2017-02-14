package com.me2me.sns.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.live.cache.MySubscribeCacheModel;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicFragment;
import com.me2me.live.model.TopicUserConfig;
import com.me2me.live.service.LiveService;
import com.me2me.sms.service.JPushService;
import com.me2me.sns.dao.LiveJdbcDao;
import com.me2me.sns.dao.SnsMybatisDao;
import com.me2me.sns.dto.*;
import com.me2me.user.dto.FollowDto;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/27.
 */
@Service
@Slf4j
public class SnsServiceImpl implements SnsService {

    @Autowired
    private SnsMybatisDao snsMybatisDao;

    @Autowired
    private UserService userService;

    @Autowired
    private LiveService liveService;

    @Autowired
    private JPushService jPushService;

    @Autowired
    private LiveJdbcDao liveJdbcDao;


    @Autowired
    private CacheService cacheService;

    
    private static final String CORE_CIRCLE_KEY = "core:circle:key";

    @Override
    public Response showMemberConsole(long owner, long topicId) {
        ShowMemberConsoleDto showMemberConsoleDto = new ShowMemberConsoleDto();
        GetSnsCircleDto dto = new GetSnsCircleDto();
        dto.setUid(owner);
        dto.setSinceId(0);
        dto.setTopicId(topicId);
        dto.setType(Specification.SnsCircle.IN.index);
        List<SnsCircleDto> list = snsMybatisDao.getSnsCircle(dto);
        for (SnsCircleDto circleDto : list) {
            ShowMemberConsoleDto.UserElement userElement = ShowMemberConsoleDto.createUserElement();
            userElement.setUid(circleDto.getUid());
            userElement.setAvatar(Constant.QINIU_DOMAIN + "/" + circleDto.getAvatar());
            userElement.setIntroduced(circleDto.getIntroduced());
            userElement.setNickName(circleDto.getNickName());
            userElement.setInternalStatus(circleDto.getInternalStatus());
            showMemberConsoleDto.getInCircle().add(userElement);
        }
        dto.setType(Specification.SnsCircle.IN.index);
        int inCount = snsMybatisDao.getSnsCircleCount(dto);
        dto.setType(Specification.SnsCircle.OUT.index);
        int outCount = snsMybatisDao.getSnsCircleCount(dto);
        dto.setType(Specification.SnsCircle.CORE.index);
        int coreCount = snsMybatisDao.getSnsCircleCount(dto);
        showMemberConsoleDto.setMembers(inCount + outCount + coreCount);
        showMemberConsoleDto.setCoreCircleMembers(coreCount);
        showMemberConsoleDto.setInCircleMembers(inCount);
        showMemberConsoleDto.setOutCircleMembers(outCount);
        return Response.success(ResponseStatus.SHOW_MEMBER_CONSOLE_SUCCESS.status, ResponseStatus.SHOW_MEMBER_CONSOLE_SUCCESS.message, showMemberConsoleDto);
    }

    @Override
    public Response showMembers(GetSnsCircleDto dto) {
        log.info("showMembers start ...");
        ShowMembersDto showMembersDto = new ShowMembersDto();
        List<SnsCircleDto> list = snsMybatisDao.getSnsCircleMember(dto);
        log.info("showMembers get data");
        buildMembers(showMembersDto, list);
        log.info("showMembers build data");
        log.info("showMembers end ...");
        return Response.success(ResponseStatus.SHOW_MEMBERS_SUCCESS.status, ResponseStatus.SHOW_MEMBERS_SUCCESS.message, showMembersDto);
    }

    private void buildMembers(ShowMembersDto showMembersDto, List<SnsCircleDto> list) {
        for (SnsCircleDto circleDto : list) {
            ShowMembersDto.UserElement userElement = showMembersDto.createUserElement();
            userElement.setUid(circleDto.getUid());
            UserProfile profile = userService.getUserProfileByUid(circleDto.getUid());
            userElement.setV_lv(profile.getvLv());
            userElement.setAvatar(Constant.QINIU_DOMAIN + "/" + circleDto.getAvatar());
            userElement.setIntroduced(circleDto.getIntroduced());
            userElement.setNickName(circleDto.getNickName());
            userElement.setInternalStatus(circleDto.getInternalStatus());
            showMembersDto.getMembers().add(userElement);
        }
    }

    @Override
    public Response circleByType(GetSnsCircleDto dto) {
        log.info("getCircleByType start ...");
        Topic topic = liveService.getTopicById(dto.getTopicId());
        long uid = dto.getUid();
        dto.setUid(topic.getUid());
        ShowSnsCircleDto showSnsCircleDto = new ShowSnsCircleDto();

        //先把自己加到核心
//            snsMybatisDao.createSnsCircle(dto.getUid(),dto.getUid(),Specification.SnsCircle.CORE.index);
        String coreCircle = topic.getCoreCircle();
        JSONArray coreCircles = JSON.parseArray(coreCircle);
        if (dto.getType() == Specification.SnsCircle.CORE.index) {
            if(dto.getSinceId()<2) {
                List<Long> uidIn = new ArrayList<>();
                for (int i = 0; i < coreCircles.size(); i++) {
                    uidIn.add(coreCircles.getLong(i));
                }
                List<UserProfile> userProfiles = userService.getUserProfilesByUids(uidIn);
                buildCoreCircle(showSnsCircleDto, userProfiles, uid, topic.getUid());
            }
        } else {
            List<SnsCircleDto> list = snsMybatisDao.getSnsCircle(dto);
            buildSnsCircle(showSnsCircleDto, list, uid, topic.getUid());
        }


        dto.setType(Specification.SnsCircle.IN.index);
        int inCount = snsMybatisDao.getSnsCircleCount(dto);
        dto.setType(Specification.SnsCircle.OUT.index);
        int outCount = snsMybatisDao.getSnsCircleCount(dto);
        //修改核心圈从topic表的core_circle字段中获取
        int coreCount = coreCircles.size();
//            dto.setType(Specification.SnsCircle.CORE.index);
//            int coreCount = snsMybatisDao.getSnsCircleCount(dto);
        showSnsCircleDto.setMembers(inCount + outCount + coreCount);
        showSnsCircleDto.setCoreCircleMembers(coreCount);
        showSnsCircleDto.setInCircleMembers(inCount);
        showSnsCircleDto.setOutCircleMembers(outCount);
        log.info("getCircleByType end ...");
       /* }
        else{
            GetSnsCircleDto dto2 = new GetSnsCircleDto();
            dto2.setUid(topic.getUid());
            dto2.setSinceId(dto.getSinceId());
            dto2.setTopicId(dto.getTopicId());
            dto2.setType(dto.getType());
            //先把自己加到核心
            snsMybatisDao.createSnsCircle(dto2.getUid(),dto2.getUid(),Specification.SnsCircle.CORE.index);
            List<SnsCircleDto> list = snsMybatisDao.getSnsCircle(dto2);
            buildSnsCircle(showSnsCircleDto, list,dto.getUid(),topic.getUid());
            dto2.setType(Specification.SnsCircle.IN.index);
            int inCount = snsMybatisDao.getSnsCircleCount(dto2);
            dto2.setType(Specification.SnsCircle.OUT.index);
            int outCount = snsMybatisDao.getSnsCircleCount(dto2);
            dto2.setType(Specification.SnsCircle.CORE.index);
            int coreCount = snsMybatisDao.getSnsCircleCount(dto2);
            showSnsCircleDto.setMembers(inCount + outCount + coreCount);
            showSnsCircleDto.setCoreCircleMembers(coreCount);
            showSnsCircleDto.setInCircleMembers(inCount);
            showSnsCircleDto.setOutCircleMembers(outCount);
            log.info("getCircleByType end ...");
        }*/

        return Response.success(showSnsCircleDto);
    }

    private void buildCoreCircle(ShowSnsCircleDto showSnsCircleDto, List<UserProfile> userProfiles, long uid, long topicUid) {
        for (UserProfile profile : userProfiles) {
            ShowSnsCircleDto.SnsCircleElement snsCircleElement = showSnsCircleDto.createElement();
            snsCircleElement.setV_lv(profile.getvLv());
            snsCircleElement.setUid(profile.getUid());
            snsCircleElement.setAvatar(Constant.QINIU_DOMAIN + "/" + profile.getAvatar());
            snsCircleElement.setIntroduced(profile.getIntroduced());
            snsCircleElement.setNickName(profile.getNickName());
            snsCircleElement.setInternalStatus(Specification.SnsCircle.CORE.index);
            int follow = userService.isFollow(profile.getUid(), uid);
            int followMe = userService.isFollow(uid, profile.getUid());
            snsCircleElement.setIsFollowed(follow);
            snsCircleElement.setIsFollowMe(followMe);
            //国王放到首位
            if (profile.getUid() == topicUid) {
                showSnsCircleDto.getCircleElements().add(0, snsCircleElement);
            } else {
                showSnsCircleDto.getCircleElements().add(snsCircleElement);
            }
        }
    }


    //topicUid为了判断是否自己是国王
    private void buildSnsCircle(ShowSnsCircleDto showSnsCircleDto, List<SnsCircleDto> list, long uid, long topicUid ) {
        for (SnsCircleDto circleDto : list) {

            ShowSnsCircleDto.SnsCircleElement snsCircleElement = showSnsCircleDto.createElement();
            UserProfile profile = userService.getUserProfileByUid(circleDto.getUid());
            snsCircleElement.setV_lv(profile.getvLv());
            snsCircleElement.setUid(circleDto.getUid());
            snsCircleElement.setAvatar(Constant.QINIU_DOMAIN + "/" + circleDto.getAvatar());
            snsCircleElement.setIntroduced(circleDto.getIntroduced());
            snsCircleElement.setNickName(circleDto.getNickName());
            snsCircleElement.setInternalStatus(circleDto.getInternalStatus());
            int follow = userService.isFollow(circleDto.getUid(), uid);
            int followMe = userService.isFollow(uid, circleDto.getUid());
            snsCircleElement.setIsFollowed(follow);
            snsCircleElement.setIsFollowMe(followMe);
            //国王放到首位
            if (circleDto.getUid() == topicUid) {
                showSnsCircleDto.getCircleElements().add(0, snsCircleElement);
            } else {
                showSnsCircleDto.getCircleElements().add(snsCircleElement);
            }
        }
    }

    private boolean inCoreCircles(JSONArray coreCircles, long uid) {
        for(int i=0;i<coreCircles.size();i++){
            if(coreCircles.getLong(i)==uid)
                return true;
        }
        return false;
    }

//    private void buildSnsCircle(ShowSnsCircleDto showSnsCircleDto, List<SnsCircleDto> list,long uid) {
//        for(SnsCircleDto circleDto : list){
//            ShowSnsCircleDto.SnsCircleElement snsCircleElement = showSnsCircleDto.createElement();
//            snsCircleElement.setUid(circleDto.getUid());
//            snsCircleElement.setAvatar(Constant.QINIU_DOMAIN + "/" + circleDto.getAvatar());
//            snsCircleElement.setIntroduced(circleDto.getIntroduced());
//            snsCircleElement.setNickName(circleDto.getNickName());
//            snsCircleElement.setInternalStatus(circleDto.getInternalStatus());
//            int follow = userService.isFollow(circleDto.getUid() ,uid);
//            int followMe = userService.isFollow(uid,circleDto.getUid());
//            snsCircleElement.setIsFollowed(follow);
//            snsCircleElement.setIsFollowMe(followMe);
//            //国王放到首位
//            if(circleDto.getUid() == uid){
//                showSnsCircleDto.getCircleElements().add(0,snsCircleElement);
//            }else {
//                showSnsCircleDto.getCircleElements().add(snsCircleElement);
//            }
//        }
//    }

    @Override
    public Response subscribed(long uid, long topicId, long topId, long bottomId, int action) {
        Topic topic = liveService.getTopicById(topicId);
        if (action == 0) {
            List<Topic> list = liveService.getTopicList(topic.getUid());
            
            List<Long> topicIds = new ArrayList<Long>();
            if(null != list && list.size() > 0){
            	for(Topic t : list){
            		topicIds.add(t.getId());
                }
            }
            if(null != topicIds && topicIds.size() > 0){
            	liveService.setLiveFromSnsFollow(uid, topicIds, 0, 0, action);
            }
            
//            for (Topic live : list) {
//                liveService.setLive2(uid, live.getId(), 0, 0, action);
//            }
            FollowDto dto = new FollowDto();
            dto.setSourceUid(uid);
            dto.setTargetUid(topic.getUid());
            dto.setAction(action);
            //关注
            userService.follow(dto);
            //保存圈子关系
            int isFollow = userService.isFollow(uid, topic.getUid());
            int internalStatus = 0;
            if (isFollow == 1) {
                internalStatus = 1;
            }
            snsMybatisDao.createSnsCircle(uid, topic.getUid(), internalStatus);
        } else if (action == 1) {
            //取消该直播的订阅
            liveService.setLive2(uid, topicId, 0, 0, action);
            //如果是核心圈取消订阅，直接踢出核心圈
            if(topic.getCoreCircle().replaceAll("[\\[|\\]]",",").indexOf(","+uid+",")>-1){
                JSONArray array = JSON.parseArray(topic.getCoreCircle());
                for (int i = 0; i < array.size(); i++) {
                    if (array.getLong(i) == uid) {
                        array.remove(i);
                    }
                }
                liveJdbcDao.updateTopic(topic.getId(),array.toJSONString());
            }
        }
        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    @Override
    public Response follow(int action, long targetUid, long sourceUid) {
    	log.info("====follow start...");
        FollowDto followDto = new FollowDto();
        followDto.setSourceUid(sourceUid);
        followDto.setTargetUid(targetUid);
        followDto.setAction(action);
        Response response = userService.follow(followDto);
        //如果关注失败者直接返回失败，不必进行下面的逻辑
        if(null == response || (response.getCode() != ResponseStatus.USER_FOLLOW_SUCCESS.status && response.getCode() != ResponseStatus.USER_CANCEL_FOLLOW_SUCCESS.status)){
        	return response;
        }
        
        List<Topic> list = liveService.getMyTopic4Follow(targetUid);
        //关注,订阅所有直播/取消所有直播订阅
//        for (Topic topic : list) {
//            liveService.setLive2(sourceUid, topic.getId(), 0, 0, action);
//        }
        List<Long> topicIds = new ArrayList<Long>();
        if(null != list && list.size() > 0){
        	for(Topic topic : list){
        		topicIds.add(topic.getId());
            }
        }
        if(null != topicIds && topicIds.size() > 0){
        	liveService.setLiveFromSnsFollow(sourceUid, topicIds, 0, 0, action);
        }
        
        //关注，默认加到圈外人
        if (action == 0) {
            // 判断人员关系,
            // 1如果他是我的粉丝则为相互圈内人
            //2.如果他不是我的粉丝，我是他的圈外人
            int isFollow = userService.isFollow(sourceUid, targetUid);
            int internalStatus = 0;
            if (isFollow == 1) {
                internalStatus = 1;
                snsMybatisDao.updateSnsCircle(sourceUid, targetUid, internalStatus);
                snsMybatisDao.createSnsCircle(targetUid, sourceUid, internalStatus);
            } else {
                snsMybatisDao.createSnsCircle(sourceUid, targetUid, internalStatus);
            }
            //取消关注，取消圈子信息
        } else if (action == 1) {
            //如果是取消关注，如果他是我粉丝，我不是他圈子里的人，他是我的圈外人
            snsMybatisDao.deleteSnsCircle(sourceUid, targetUid);
            // 判断人员关系
            int isFollow = userService.isFollow(sourceUid, targetUid);
            int internalStatus = 0;
            if (isFollow == 1) {
                snsMybatisDao.updateSnsCircle(targetUid, sourceUid, internalStatus);
            }
        }
        log.info("====follow end...");
        return response;
    }

    @Override
    public Response modifyCircle(long owner, long topicId, long uid, int action) {
        log.info("modify circle start ... action:"+action+";topicId:"+topicId);
        //兼容老版本
//        String snsOnline = cacheService.get("version:2.1.1:online");

        if (action == 0) {
            snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.IN.index);
            //关注此人
            follow(0, uid, owner);
            liveService.setLive2(uid, topicId, 0, 0, 0);
            liveService.deleteFavoriteDelete(uid, topicId);
            createFragment(owner, topicId, uid);
        } else {
            if (action == 1) {
                Topic topic = liveService.getTopicById(topicId);
                //查询核心圈人数，人数不能超过10人
                String coreCircle = StringUtils.isEmpty(topic.getCoreCircle()) ? "[" + topic.getUid() + "]" : topic.getCoreCircle();
                JSONArray array = JSON.parseArray(coreCircle);

                String limit = cacheService.get(CORE_CIRCLE_KEY);
                int limitNum = 12;
                if(null != limit && !"".equals(limit)){
                	limitNum = Integer.valueOf(limit);
                }
                
                if (array.size() >= limitNum)
                    return Response.failure(ResponseStatus.SNS_CORE_CIRCLE_IS_FULL.status, ResponseStatus.SNS_CORE_CIRCLE_IS_FULL.message);
                else {
                    boolean contain = false;
                    for (int i = 0; i < array.size(); i++) {
                        if (array.getLong(i) == uid) {
                            contain = true;
                            break;
                        }
                    }
                    if (contain) {
                        return Response.failure(ResponseStatus.IS_ALREADY_SNS_CORE.status, ResponseStatus.IS_ALREADY_SNS_CORE.message);
                    } else {
                        array.add(uid);
                        // 更新直播核心圈关系
                        liveJdbcDao.updateTopic(topicId, array.toString());
                        snsMybatisDao.deleteSnsCircle(uid, owner);
                    }
                }
               //关注此人
                follow(0, uid, owner);
                liveService.setLive2(uid, topicId, 0, 0, 0);
                //推送被邀请的人
                UserProfile userProfile = userService.getUserProfileByUid(owner);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType", Specification.PushMessageType.CORE_CIRCLE.index);
                jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
                jsonObject.addProperty("topicId", topicId);
                jsonObject.addProperty("contentType", topic.getType());
                jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//既然是邀请核心圈，那么已经是核心圈了，所以直接设置核心圈标识
                jsonObject.addProperty("fromInternalStatus", Specification.SnsCircle.CORE.index);//只有国王才能邀请核心圈，所以直接设置核心圈标识
                String alias = String.valueOf(uid);
                String review = "你已加入『" + topic.getTitle() + "』核心圈";
                String message = "邀请我加入核心圈";
                
                if(this.checkTopicPush(topicId, uid)){
                	userService.pushWithExtra(alias, review, JPushUtils.packageExtra(jsonObject));
                }
                snsRemind(uid, userProfile.getUid(), message, topic.getId(), Specification.UserNoticeType.LIVE_INVITED.index);

                //增加列表更新红点
                MySubscribeCacheModel cacheModel = new MySubscribeCacheModel(uid, topicId + "", "1");
                cacheService.hSet(cacheModel.getKey(), cacheModel.getField(), cacheModel.getValue());
            } else if (action == 2) {
                snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.IN.index);
                createFragment(owner, topicId, uid);
            } else if (action == 3) {
                snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.OUT.index);
                //取消关注此人，取消此人直播的订阅
                follow(1, uid, owner);
            } else if (action == 4) {
                snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.OUT.index);
                liveService.setLive2(uid, topicId, 0, 0, 0);
                liveService.deleteFavoriteDelete(uid, topicId);
                createFragment(owner, topicId, uid);
            } else if (action == 5) {
                Topic topic = liveService.getTopicById(topicId);
                JSONArray array = JSON.parseArray(topic.getCoreCircle());
                for (int i = 0; i < array.size(); i++) {
                    if (array.getLong(i) == uid) {
                        array.remove(i);
                    }
                }
                liveJdbcDao.updateTopic(topicId, array.toString());

                //人员原来是什么样的关系，还是什么样的关系
                int isFollow = userService.isFollow(owner, uid);
                int isFollowMe = userService.isFollow(uid, owner);
                if (isFollow == 1 && isFollowMe == 1) {
                    snsMybatisDao.createSnsCircle(uid, owner, Specification.SnsCircle.IN.index);
                } else if (isFollow == 1 && isFollowMe == 0) {
                    snsMybatisDao.createSnsCircle(uid, owner, Specification.SnsCircle.OUT.index);
                }

                UserProfile userProfile = userService.getUserProfileByUid(owner);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType", Specification.PushMessageType.REMOVE_CORE_CIRCLE.index);
                jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
                jsonObject.addProperty("topicId", topicId);
                topic = liveService.getTopicById(topicId);//已经更新过了。。重新查询一下
                jsonObject.addProperty("contentType", topic.getType());
                jsonObject.addProperty("internalStatus", this.getInternalStatus(topic, uid));
                jsonObject.addProperty("fromInternalStatus", Specification.SnsCircle.CORE.index);//只有国王才能移出核心圈，所以直接设置核心圈标识
                String alias = String.valueOf(uid);
                String review = "你被移出『" + topic.getTitle() + "』核心圈";
                String message = "将我从核心圈移除";
                
                if(this.checkTopicPush(topicId, uid)){
                	userService.pushWithExtra(alias, review, JPushUtils.packageExtra(jsonObject));
                }

                snsRemind(uid, userProfile.getUid(), message, topic.getId(), Specification.UserNoticeType.REMOVE_SNS_CIRCLE.index);
            } else if(action == 6) {
            	Topic topic = liveService.getTopicById(topicId);
                JSONArray array = JSON.parseArray(topic.getCoreCircle());
                boolean flag = false;
                for (int i = 0; i < array.size(); i++) {
                    if (array.getLong(i) == uid) {
                        array.remove(i);
                        flag = true;
                    }
                }
                if(flag){
	                liveJdbcDao.updateTopic(topicId, array.toString());
	                
	                //发送消息，告诉国王我退出了核心圈
	                snsRemind(topic.getUid(), uid, "退出了核心圈", topic.getId(), Specification.UserNoticeType.CORE_CIRCLE_NOTICE.index);
	                //推送告知国王我退出了核心圈
	                if(this.checkTopicPush(topicId, topic.getUid())){
	                	UserProfile userProfile = userService.getUserProfileByUid(uid);
	                	String alias = String.valueOf(topic.getUid());
	                	String review = userProfile.getNickName()+"退出了『" + topic.getTitle() + "』核心圈";
	                	JsonObject jsonObject = new JsonObject();
	                    jsonObject.addProperty("messageType", Specification.PushMessageType.QUIT_CORE_CIRCLE.index);
	                    jsonObject.addProperty("type", Specification.PushObjectType.LIVE.index);
	                    jsonObject.addProperty("topicId", topicId);
	                    jsonObject.addProperty("contentType", topic.getType());
	                    jsonObject.addProperty("internalStatus", Specification.SnsCircle.CORE.index);//国王自身肯定是核心圈人物啊
	                	userService.pushWithExtra(alias, review, JPushUtils.packageExtra(jsonObject));
	                }
                }
            }
        }
        log.info("modify circle end ...");
        return Response.success(ResponseStatus.MODIFY_CIRCLE_SUCCESS.status, ResponseStatus.MODIFY_CIRCLE_SUCCESS.message);
    }
    
    //判断核心圈身份
    private int getInternalStatus(Topic topic, long uid) {
        String coreCircle = topic.getCoreCircle();
        JSONArray array = JSON.parseArray(coreCircle);
        int internalStatus = 0;
        for (int i = 0; i < array.size(); i++) {
            if (array.getLong(i) == uid) {
                internalStatus = Specification.SnsCircle.CORE.index;
                break;
            }
        }
        if (internalStatus == 0) {
            internalStatus = userService.getUserInternalStatus(uid, topic.getUid());
        }

        return internalStatus;
    }

    private void snsRemind(long targetUid, long sourceUid, String review, long cid, int type) {
        if (targetUid == sourceUid) {
            return;
        }
        UserProfile userProfile = userService.getUserProfileByUid(sourceUid);
        UserProfile customerProfile = userService.getUserProfileByUid(targetUid);
        UserNotice userNotice = new UserNotice();
        userNotice.setFromNickName(userProfile.getNickName());
        userNotice.setFromAvatar(userProfile.getAvatar());
        userNotice.setFromUid(userProfile.getUid());
        userNotice.setToNickName(customerProfile.getNickName());
        userNotice.setCid(cid);

        Topic topic = liveService.getTopicById(cid);
        userNotice.setCoverImage(topic.getLiveImage());

        userNotice.setSummary("");
        userNotice.setToUid(customerProfile.getUid());
        userNotice.setLikeCount(0);
        userNotice.setReview(review);
        userNotice.setTag("");
        userNotice.setNoticeType(type);
        userNotice.setReadStatus(0);
        
        if(type == Specification.UserNoticeType.CORE_CIRCLE_NOTICE.index){
        	//核心圈通知
        	JSONObject obj = new JSONObject();
        	obj.put("textImage", topic.getLiveImage());
        	obj.put("textTitle", topic.getTitle());
        	obj.put("textType", topic.getType());
        	obj.put("textTopicId", topic.getId());
        	obj.put("coverImage", topic.getLiveImage());
        	obj.put("coverTitle", topic.getTitle());
        	obj.put("coverType", topic.getType());
        	obj.put("coverTopicId", topic.getId());
        	userNotice.setExtra(obj.toJSONString());
        }
        userService.createUserNotice(userNotice);

        if(type == Specification.UserNoticeType.CORE_CIRCLE_NOTICE.index
        		|| type == Specification.UserNoticeType.LIVE_INVITED.index
        		|| type == Specification.UserNoticeType.REMOVE_SNS_CIRCLE.index){
        	//增加系统消息红点
        	cacheService.set("my:notice:level2:"+targetUid, "1");
        }
        
        UserTips userTips = new UserTips();
        userTips.setUid(targetUid);
        userTips.setType(type);
        UserTips tips = userService.getUserTips(userTips);
        if (tips == null) {
            userTips.setCount(1);
            userService.createUserTips(userTips);
        } else {
            tips.setCount(tips.getCount() + 1);
            userService.modifyUserTips(tips);
        }
        userService.noticeCountPush(targetUid);
    }

    private void createFragment(long owner, long topicId, long uid) {
        SpeakDto speakDto = new SpeakDto();
        speakDto.setUid(owner);
        speakDto.setType(Specification.LiveSpeakType.INVITED.index);
        speakDto.setAtUid(uid);
        TopicFragment fragment = liveService.getLastTopicFragmentByUid(topicId, owner);
        if (fragment != null) {
            speakDto.setBottomId(fragment.getId());
            speakDto.setTopId(fragment.getId());
        } else {
            speakDto.setBottomId(0);
            speakDto.setTopId(0);
        }
        speakDto.setContentType(Specification.LiveContent.TEXT.index);
        UserProfile userProfile = userService.getUserProfileByUid(owner);
        UserProfile fans = userService.getUserProfileByUid(uid);
        speakDto.setFragment("国王" + userProfile.getNickName() + "邀请了" + fans.getNickName() + "加入此直播");
        speakDto.setTopicId(topicId);
        speakDto.setType(Specification.LiveSpeakType.INVITED.index);
        liveService.speak(speakDto);
    }
    
    private boolean checkTopicPush(long topicId, long uid){
    	TopicUserConfig tuc = liveService.getTopicUserConfigByTopicIdAndUid(topicId, uid);
    	if(null != tuc && tuc.getPushType().intValue() == 1){
    		return false;
    	}
    	return true;
    }
}
