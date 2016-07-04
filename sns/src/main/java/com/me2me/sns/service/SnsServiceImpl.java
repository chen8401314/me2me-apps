package com.me2me.sns.service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.live.model.Topic;
import com.me2me.live.service.LiveService;
import com.me2me.sns.dao.SnsMybatisDao;
import com.me2me.sns.dto.*;
import com.me2me.user.dto.FollowDto;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/27.
 */
@Service
public class SnsServiceImpl implements SnsService {

    @Autowired
    private SnsMybatisDao snsMybatisDao;

    @Autowired
    private UserService userService;

    @Autowired
    private LiveService liveService;

    @Override
    public Response showMemberConsole(long owner,long topicId) {
        ShowMemberConsoleDto showMemberConsoleDto = new ShowMemberConsoleDto();
        GetSnsCircleDto dto = new GetSnsCircleDto();
        dto.setUid(owner);
        dto.setSinceId(0);
        dto.setTopicId(topicId);
        dto.setType(Specification.SnsCircle.IN.index);
        List<SnsCircleDto> list = snsMybatisDao.getSnsCircle(dto);
        for(SnsCircleDto circleDto : list){
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
        return Response.success(ResponseStatus.SHOW_MEMBER_CONSOLE_SUCCESS.status,ResponseStatus.SHOW_MEMBER_CONSOLE_SUCCESS.message,showMemberConsoleDto);
    }

    @Override
    public Response showMembers(long owner,long topicId ,long sinceId,int type) {
        ShowMembersDto showMembersDto = new ShowMembersDto();
        GetSnsCircleDto dto = new GetSnsCircleDto();
        dto.setUid(owner);
        dto.setSinceId((sinceId-1)*10);
        dto.setTopicId(topicId);
        dto.setType(type);
        List<SnsCircleDto> list = snsMybatisDao.getSnsCircleMember(dto);
        for(SnsCircleDto circleDto : list){
            ShowMembersDto.UserElement userElement = showMembersDto.createUserElement();
            userElement.setUid(circleDto.getUid());
            userElement.setAvatar(Constant.QINIU_DOMAIN + "/" + circleDto.getAvatar());
            userElement.setIntroduced(circleDto.getIntroduced());
            userElement.setNickName(circleDto.getNickName());
            userElement.setInternalStatus(circleDto.getInternalStatus());
            showMembersDto.getMembers().add(userElement);
        }
        return Response.success(ResponseStatus.SHOW_MEMBERS_SUCCESS.status,ResponseStatus.SHOW_MEMBERS_SUCCESS.message,showMembersDto);
    }

    @Override
    public Response getCircleByType(long owner, long topicId, long sinceId,int type) {
        ShowSnsCircleDto showSnsCircleDto = new ShowSnsCircleDto();
        GetSnsCircleDto dto = new GetSnsCircleDto();
        dto.setUid(owner);
        dto.setSinceId((sinceId - 1) * 10);
        dto.setTopicId(topicId);
        dto.setType(type);
        List<SnsCircleDto> list = snsMybatisDao.getSnsCircle(dto);
        for(SnsCircleDto circleDto : list){
            ShowSnsCircleDto.SnsCircleElement snsCircleElement = showSnsCircleDto.createElement();
            snsCircleElement.setUid(circleDto.getUid());
            snsCircleElement.setAvatar(Constant.QINIU_DOMAIN + "/" + circleDto.getAvatar());
            snsCircleElement.setIntroduced(circleDto.getIntroduced());
            snsCircleElement.setNickName(circleDto.getNickName());
            snsCircleElement.setInternalStatus(circleDto.getInternalStatus());
            showSnsCircleDto.getCircleElements().add(snsCircleElement);

        }
        dto.setType(Specification.SnsCircle.IN.index);
        int inCount = snsMybatisDao.getSnsCircleCount(dto);
        dto.setType(Specification.SnsCircle.OUT.index);
        int outCount = snsMybatisDao.getSnsCircleCount(dto);
        dto.setType(Specification.SnsCircle.CORE.index);
        int coreCount = snsMybatisDao.getSnsCircleCount(dto);
        showSnsCircleDto.setMembers(inCount + outCount + coreCount);
        showSnsCircleDto.setCoreCircleMembers(coreCount);
        showSnsCircleDto.setInCircleMembers(inCount);
        showSnsCircleDto.setOutCircleMembers(outCount);
        return Response.success(showSnsCircleDto);
    }

    @Override
    public Response subscribed(long uid,long topicId, long topId, long bottomId, int action) {
        Topic topic = liveService.getTopicById(topicId);
        if(action == 0) {
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
            int isFollow = userService.isFollow(topic.getUid(),uid);
            int internalStatus = 0;
            if(isFollow == 1){
                internalStatus = 1;
            }
            snsMybatisDao.createSnsCircle(uid,internalStatus,topic.getUid());
        }else if(action == 1){
            //取消该直播的关注
            liveService.setLive2(uid, topicId, 0, 0, action);
        }
        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status,ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    @Override
    public Response follow(int action, long targetUid, long sourceUid) {
        FollowDto followDto = new FollowDto();
        followDto.setSourceUid(sourceUid);
        followDto.setTargetUid(targetUid);
        followDto.setAction(action);
        List<Topic> list = liveService.getTopicList(targetUid);
        //关注,订阅所有直播/取消所有直播订阅
        for (Topic topic : list) {
            liveService.setLive2(sourceUid, topic.getId(), 0, 0,action);
        }
        //关注，默认加到圈外人
        if(action == 0) {
            // 判断人员关系
            int isFollow = userService.isFollow(targetUid,sourceUid);
            int internalStatus = 0;
            if(isFollow == 1){
                internalStatus = 1;
                snsMybatisDao.updateSnsCircle(targetUid,sourceUid,internalStatus);
            }
            snsMybatisDao.createSnsCircle(sourceUid,internalStatus,targetUid);
            //取消关注，取消圈子信息
        }else if(action == 1){
            snsMybatisDao.deleteSnsCircle(sourceUid,targetUid);
        }
        return userService.follow(followDto);
    }

    @Override
    public Response modifyCircle(long owner,long topicId ,long uid,int action) {
        if(action  == 0) {
            snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.IN.index);
            //订阅此直播
            liveService.setLive2(uid, topicId, 0, 0,action);
        }else if(action == 1){
            snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.CORE.index);
            //关注此人
            FollowDto followDto = new FollowDto();
            followDto.setSourceUid(owner);
            followDto.setTargetUid(uid);
            followDto.setAction(0);
            List<Topic> list = liveService.getTopicList(uid);
            for (Topic topic : list) {
                //订阅所有直播
                liveService.setLive2(owner, topic.getId(), 0, 0,0);
            }
            return userService.follow(followDto);
        }else if(action == 2){
            snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.IN.index);
        }else if(action == 3){
            snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.OUT.index);
            //取消关注此人，取消此人直播的订阅
            FollowDto followDto = new FollowDto();
            followDto.setSourceUid(owner);
            followDto.setTargetUid(uid);
            followDto.setAction(1);
            List<Topic> list = liveService.getTopicList(uid);
            for (Topic topic : list) {
                //订阅所有直播
                liveService.setLive2(owner, topic.getId(), 0, 0,1);
            }
            userService.follow(followDto);
        }else if(action == 4){
            snsMybatisDao.updateSnsCircle(uid, owner, Specification.SnsCircle.OUT.index);
            liveService.setLive2(uid, topicId, 0, 0,action);
        }
        return Response.success(ResponseStatus.MODIFY_CIRCLE_SUCCESS.status,ResponseStatus.MODIFY_CIRCLE_SUCCESS.message);
    }
}
