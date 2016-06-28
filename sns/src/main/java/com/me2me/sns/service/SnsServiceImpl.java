package com.me2me.sns.service;

import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.sns.dao.SnsMybatisDao;
import com.me2me.sns.dto.*;
import com.me2me.sns.model.SnsCircle;
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

    @Override
    public Response showMemberConsole(long owner,long topicId ,long sinceId) {
        ShowMemberConsoleDto showMemberConsoleDto = new ShowMemberConsoleDto();
        List<SnsCircle> coreList = snsMybatisDao.getSnsCircle(owner,topicId,sinceId, Specification.SnsCircle.CORE.index);
        List<SnsCircle> inList = snsMybatisDao.getSnsCircle(owner,topicId,sinceId, Specification.SnsCircle.IN.index);
        List<SnsCircle> outList = snsMybatisDao.getSnsCircle(owner,topicId,sinceId, Specification.SnsCircle.OUT.index);
        showMemberConsoleDto.setMembers(100000000);
        ShowMemberConsoleDto.UserElement user = showMemberConsoleDto.createUserElement();
        user.setUid(315);
        user.setAvatar(Constant.QINIU_DOMAIN + "/" + "FpXdLCD5Nhos0NbWPaLHcegzAiMe");
        user.setNickName("小小宝");
        user.setInternalStatus(0);
        user.setIntroduced("我是一个小小宝");
        ShowMemberConsoleDto.UserElement user2 = showMemberConsoleDto.createUserElement();
        user2.setUid(310);
        user2.setAvatar(Constant.QINIU_DOMAIN + "/" + "FpXdLCD5Nhos0NbWPaLHcegzAiMe");
        user2.setNickName("小小宝1");
        user2.setInternalStatus(1);
        user2.setIntroduced("我是一个小小宝");
        ShowMemberConsoleDto.UserElement user3 = showMemberConsoleDto.createUserElement();
        user3.setUid(311);
        user3.setAvatar(Constant.QINIU_DOMAIN + "/" + "FpXdLCD5Nhos0NbWPaLHcegzAiMe");
        user3.setNickName("小小宝2");
        user3.setInternalStatus(2);
        user3.setIntroduced("我是一个小小宝");
        showMemberConsoleDto.getCoreCircle().add(user);
        showMemberConsoleDto.getInCircle().add(user2);
        showMemberConsoleDto.getOutCircle().add(user3);
        return Response.success(ResponseStatus.SHOW_MEMBER_CONSOLE_SUCCESS.status,ResponseStatus.SHOW_MEMBER_CONSOLE_SUCCESS.message,showMemberConsoleDto);
    }

    @Override
    public Response showMembers(long owner,long topicId ,long sinceId) {
        ShowMembersDto showMembersDto = new ShowMembersDto();
        ShowMembersDto.UserElement user = showMembersDto.createUserElement();
        user.setUid(315);
        user.setAvatar(Constant.QINIU_DOMAIN + "/" + "FpXdLCD5Nhos0NbWPaLHcegzAiMe");
        user.setNickName("小小宝");
        user.setInternalStatus(0);
        user.setIntroduced("我是一个小小宝");
        showMembersDto.getMembers().add(user);
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
    public Response modifyCircle(long owner,long topicId ,long uid,int action) {
        return Response.success(ResponseStatus.MODIFY_CIRCLE_SUCCESS.status,ResponseStatus.MODIFY_CIRCLE_SUCCESS.message);
    }
}
