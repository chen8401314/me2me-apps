package com.me2me.sns.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.me2me.common.Constant;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.live.dao.LiveMybatisDao;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.model.Topic;
import com.me2me.live.model.TopicFragment;
import com.me2me.live.service.LiveService;
import com.me2me.sms.service.JPushService;
import com.me2me.sns.dao.SnsMybatisDao;
import com.me2me.sns.dto.*;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.dto.FollowDto;
import com.me2me.user.model.JpushToken;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;
import com.me2me.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private LiveMybatisDao liveMybatisDao;

    @Autowired
    private UserMybatisDao userMybatisDao;


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
        ShowSnsCircleDto showSnsCircleDto = new ShowSnsCircleDto();
        if (topic.getUid() != dto.getUid())
            dto.setUid(topic.getUid());
        //先把自己加到核心
//            snsMybatisDao.createSnsCircle(dto.getUid(),dto.getUid(),Specification.SnsCircle.CORE.index);
        String coreCircle = topic.getCoreCircle();
        JsonArray coreCircles = new JsonParser().parse(coreCircle).getAsJsonArray();
        if (dto.getType() == Specification.SnsCircle.CORE.index) {
            List<Long> uidIn =  new Gson().fromJson(coreCircles, new TypeToken<List<Long>>() {}.getType());
            List<UserProfile> userProfiles = userMybatisDao.getUserProfilesByUids(uidIn);
            buildCoreCircle(showSnsCircleDto, userProfiles, dto.getUid(), topic.getUid());
        } else {
            List<SnsCircleDto> list = snsMybatisDao.getSnsCircle(dto);
            buildSnsCircle(showSnsCircleDto, list, dto.getUid(), topic.getUid());
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

    private void buildCoreCircle(ShowSnsCircleDto showSnsCircleDto, List<UserProfile> userProfiles, long uid, Long topicUid) {
        for (UserProfile profile : userProfiles) {
            ShowSnsCircleDto.SnsCircleElement snsCircleElement = showSnsCircleDto.createElement();
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
    private void buildSnsCircle(ShowSnsCircleDto showSnsCircleDto, List<SnsCircleDto> list, long uid, long topicUid) {
        for (SnsCircleDto circleDto : list) {
            ShowSnsCircleDto.SnsCircleElement snsCircleElement = showSnsCircleDto.createElement();
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
            for (Topic live : list) {
                liveService.setLive2(uid, live.getId(), 0, 0, action);
            }
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
        }
        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status, ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    @Override
    public Response follow(int action, long targetUid, long sourceUid) {
        FollowDto followDto = new FollowDto();
        followDto.setSourceUid(sourceUid);
        followDto.setTargetUid(targetUid);
        followDto.setAction(action);
        Response response = userService.follow(followDto);
        List<Topic> list = liveService.getMyTopic4Follow(targetUid);
        //关注,订阅所有直播/取消所有直播订阅
        for (Topic topic : list) {
            liveService.setLive2(sourceUid, topic.getId(), 0, 0, action);
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
        return response;
    }

    @Override
    public Response modifyCircle(long owner, long topicId, long uid, int action) {
        if (action == 0) {
            snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.IN.index);
            //关注此人
            follow(0, uid, owner);
            liveService.setLive2(uid, topicId, 0, 0, 0);
            liveService.deleteFavoriteDelete(uid, topicId);
            createFragment(owner, topicId, uid);
        } else if (action == 1) {
            Topic topic = liveService.getTopicById(topicId);
            //查询核心圈人数，人数不能超过10人
            String coreCircle = StringUtils.isEmpty(topic.getCoreCircle()) ? "[" + topic.getUid() + "]" : topic.getCoreCircle();
            JsonArray array = new JsonParser().parse(coreCircle).getAsJsonArray();
            if (array.size() == 10)
                return Response.failure(ResponseStatus.SNS_CORE_CIRCLE_IS_FULL.status, ResponseStatus.SNS_CORE_CIRCLE_IS_FULL.message);
            else {
                JsonPrimitive element = new JsonPrimitive(uid);
                if (array.contains(element)) {
                    return Response.failure(ResponseStatus.IS_ALREADY_SNS_CORE.status, ResponseStatus.IS_ALREADY_SNS_CORE.message);
                } else {
                    array.add(element);
                    topic.setCoreCircle(new Gson().toJson(array));
                    liveMybatisDao.updateTopic(topic);
                    snsMybatisDao.deleteSnsCircle(uid,owner);
                }
            }
           /* List<SnsCircle> snsCircles = snsMybatisDao.getSnsCircle(owner,topicId,Specification.SnsCircle.CORE.index);
            if(snsCircles.size() == 10){
                return Response.failure(ResponseStatus.SNS_CORE_CIRCLE_IS_FULL.status,ResponseStatus.SNS_CORE_CIRCLE_IS_FULL.message);
            }else {
                SnsCircle snsCircle = snsMybatisDao.getMySnsCircle(uid,topicId,owner,Specification.SnsCircle.CORE.index);
                if(snsCircle != null){
                    return Response.failure(ResponseStatus.IS_ALREADY_SNS_CORE.status,ResponseStatus.IS_ALREADY_SNS_CORE.message);
                }else {
                    snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.CORE.index);
                }
            }*/
//            //关注此人
//            follow(0,uid,owner);
            liveService.setLive2(uid, topicId, 0, 0, 0);
            liveService.deleteFavoriteDelete(uid, topicId);
            //修改人员进入核心圈,不修改人员的关注，订阅关系。
            //推送被邀请的人
            UserProfile userProfile = userService.getUserProfileByUid(owner);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType", Specification.PushMessageType.CORE_CIRCLE.index);
            String alias = String.valueOf(uid);
            String review = userProfile.getNickName() + "邀请你成为" + topic.getTitle() + "的核心圈成员";
            String message = userProfile.getNickName() + "邀请我加入核心圈";
            jPushService.payloadByIdExtra(alias, review, JPushUtils.packageExtra(jsonObject));
            snsRemind(uid, userProfile.getUid(), message, topic.getId(), Specification.UserNoticeType.LIVE_INVITED.index);
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
            Topic topic = liveMybatisDao.getTopicById(topicId);
            JsonArray array = new JsonParser().parse(topic.getCoreCircle()).getAsJsonArray();
            array.remove(new JsonPrimitive(uid));
            topic.setCoreCircle(new Gson().toJson(array));
            liveMybatisDao.updateTopic(topic);

            //人员原来是什么样的关系，还是什么样的关系
            int isFollow = userService.isFollow(owner, uid);
            int isFollowMe = userService.isFollow(uid, owner);
            if (isFollow == 1 && isFollowMe == 1) {
                snsMybatisDao.createSnsCircle(uid, owner, Specification.SnsCircle.IN.index);
            } else if (isFollow == 1 && isFollowMe == 0) {
                snsMybatisDao.createSnsCircle(uid, owner, Specification.SnsCircle.OUT.index);
            }
        }
        return Response.success(ResponseStatus.MODIFY_CIRCLE_SUCCESS.status, ResponseStatus.MODIFY_CIRCLE_SUCCESS.message);
    }

    public void snsRemind(long targetUid, long sourceUid, String review, long cid, int type) {
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
        userNotice.setReadStatus(userNotice.getReadStatus());
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
        userService.createUserNotice(userNotice);

        UserTips userTips = new UserTips();
        userTips.setUid(targetUid);
        userTips.setType(type);
        UserTips tips = userService.getUserTips(userTips);
        if (tips == null) {
            userTips.setCount(1);
            userService.createUserTips(userTips);
            //修改推送为极光推送,兼容老版本
            JpushToken jpushToken = userService.getJpushTokeByUid(targetUid);
            if (jpushToken != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("count", "1");
                String alias = String.valueOf(targetUid);
                jPushService.payloadByIdForMessage(alias, jsonObject.toString());
            }

        } else {
            tips.setCount(tips.getCount() + 1);
            userService.modifyUserTips(tips);
            //修改推送为极光推送,兼容老版本
            JpushToken jpushToken = userService.getJpushTokeByUid(targetUid);
            if (jpushToken != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("count", "1");
                String alias = String.valueOf(targetUid);
                jPushService.payloadByIdForMessage(alias, jsonObject.toString());
            }
        }

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
}
