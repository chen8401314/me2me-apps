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
        dto.setSinceId((sinceId-1)*10);
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
        showSnsCircleDto.setCircleCount(snsMybatisDao.getSnsCircleCount(dto));
        return Response.success(showSnsCircleDto);
    }

    @Override
    public Response subscribed(long uid,long topicId, long topId, long bottomId, int action) {
        Topic topic = liveService.getTopicById(topicId);
        List<Topic> list = liveService.getTopicList(topic.getUid());
        for(Topic live : list){
            //订阅所有直播
            liveService.setLive2(uid,live.getId(),0,0,action);
        }
        FollowDto dto = new FollowDto();
        dto.setSourceUid(uid);
        dto.setTargetUid(topic.getUid());
        dto.setAction(action);
        //关注
        userService.follow(dto);
        int isFollow = userService.isFollow(topic.getUid(),uid);
        int internalStatus = 0;
        if(isFollow == 1){
            internalStatus = 1;
        }
        //关注
        if(action == 0) {
            snsMybatisDao.createSnsCircle(uid,internalStatus,topic.getUid());
            //取消关注取消圈子信息
        }else if(action == 1){
            snsMybatisDao.deleteSnsCircle(uid,topic.getUid());
        }
        liveService.setLive2(uid, topicId, topId, bottomId, action);
        return Response.success(ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.status,ResponseStatus.SET_LIVE_FAVORITE_SUCCESS.message);
    }

    @Override
    public Response follow(int action, long targetUid, long sourceUid) {
        FollowDto followDto = new FollowDto();
        followDto.setSourceUid(sourceUid);
        followDto.setTargetUid(targetUid);
        followDto.setAction(action);
        List<Topic> list = liveService.getTopicList(targetUid);
        for (Topic topic : list) {
            //订阅所有直播
            liveService.setLive2(sourceUid, topic.getId(), 0, 0,action);
        }
        //关注
        if(action == 0) {
            snsMybatisDao.createSnsCircle(sourceUid,0,targetUid);
            //取消关注取消圈子信息
        }else if(action == 1){
            snsMybatisDao.deleteSnsCircle(sourceUid,targetUid);
        }
        return userService.follow(followDto);
    }

    @Override
    public Response modifyCircle(long owner,long topicId ,long uid,int internalStatus) {
        snsMybatisDao.updateSnsCircle(uid ,owner ,internalStatus);
        return Response.success(ResponseStatus.MODIFY_CIRCLE_SUCCESS.status,ResponseStatus.MODIFY_CIRCLE_SUCCESS.message);
    }
}
