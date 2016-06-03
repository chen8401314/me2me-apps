package com.me2me.activity.service;

import com.me2me.activity.dao.ActivityMybatisDao;
import com.me2me.activity.dto.*;
import com.me2me.activity.model.ActivityWithBLOBs;
import com.me2me.activity.model.UserActivity;
import com.me2me.common.Constant;
import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.user.model.UserProfile;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMybatisDao activityMybatisDao;

    @Autowired
    private UserService userService;

    @Override
    public Response createActivity(CreateActivityDto createActivityDto) {
        ActivityWithBLOBs activity = new ActivityWithBLOBs();
        activity.setActivityCover(createActivityDto.getCover());
        activity.setActivityHashTitle(createActivityDto.getHashTitle());
        activity.setActivityTitle(createActivityDto.getTitle());
        activity.setStartTime(createActivityDto.getStartTime());
        activity.setEndTime(createActivityDto.getEndTime());
        activity.setIssue(createActivityDto.getIssue());
        activity.setActivityContent(createActivityDto.getContent());
        activity.setUid(createActivityDto.getUid());
        activity.setStatus(Specification.ActivityStatus.STOP.index);
        activityMybatisDao.saveActivity(activity);
        return Response.success();
    }

    @Override
    public Response showActivity(int page, int pageSize,String keyword) {
        ShowActivityDto showActivityDto = new ShowActivityDto();
        List<ActivityWithBLOBs> list = activityMybatisDao.showActivity(page,pageSize,keyword);
        for(ActivityWithBLOBs activity : list){
            ShowActivityDto.ActivityElement element = showActivityDto.createElement();
            element.setUid(activity.getUid());
            element.setId(activity.getId());
            element.setStartTime(activity.getStartTime());
            element.setEndTime(activity.getEndTime());
            element.setIssue(activity.getIssue());
            element.setHashTitle(activity.getActivityHashTitle());
            element.setContent(activity.getActivityContent());
            element.setTitle(activity.getActivityTitle());
            element.setStatus(activity.getStatus());
            element.setInternalStatus(activity.getInternalStatus());
            element.setActivityNoticeTitle(activity.getActivityNoticeTitle());
            element.setActivityResult(activity.getActivityResult());
            element.setActivityCover(Constant.QINIU_DOMAIN + "/" +activity.getActivityNoticeCover());
            showActivityDto.getResult().add(element);
        }
        showActivityDto.setTotal(activityMybatisDao.total(keyword));
        int totalPage = (activityMybatisDao.total(keyword) + pageSize - 1)/pageSize;
        showActivityDto.setTotalPage(totalPage);
        return Response.success(200,"数据加载成功！",showActivityDto);
    }

    @Override
    public List<ActivityWithBLOBs> getActivityTop5(){
        return activityMybatisDao.getActivityTop5();
    }

    @Override
    public Response getActivity(int sinceId,long uid) {
        ShowActivitiesDto showActivitiesDto = new ShowActivitiesDto();
        List<ActivityWithBLOBs> list = activityMybatisDao.getActivity(sinceId);
        for(ActivityWithBLOBs activity : list){
            ShowActivitiesDto.ActivityElement activityElement = ShowActivitiesDto.createActivityElement();
            activityElement.setUid(activity.getUid());
            activityElement.setTitle(activity.getActivityHashTitle());
            String cover = activity.getActivityCover();
            if(!StringUtils.isEmpty(cover)) {
                activityElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + cover);
            }
            UserProfile userProfile = userService.getUserProfileByUid(activity.getUid());
            activityElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            activityElement.setNickName(userProfile.getNickName());
            activityElement.setId(activity.getId());
            activityElement.setUpdateTime(activity.getUpdateTime());
            activityElement.setIsFollowed(userService.isFollow(activity.getUid(),uid));
            showActivitiesDto.getActivityData().add(activityElement);
        }
        return Response.success(showActivitiesDto);
    }

    @Override
    public ActivityWithBLOBs loadActivityById(long id) {
        return activityMybatisDao.getActivityById(id);
    }

    @Override
    public void modifyActivity(ActivityWithBLOBs activity) {
        activityMybatisDao.updateActivity(activity);
    }

    @Override
    public void createActivityNotice(CreateActivityNoticeDto createActivityNoticeDto) {
        ActivityWithBLOBs activityWithBLOBs = loadActivityById(createActivityNoticeDto.getId());
        activityWithBLOBs.setActivityNoticeCover(createActivityNoticeDto.getActivityNoticeCover());
        activityWithBLOBs.setActivityNoticeTitle(createActivityNoticeDto.getActivityNoticeTitle());
        activityWithBLOBs.setActivityResult(createActivityNoticeDto.getActivityResult());
        activityWithBLOBs.setInternalStatus(Specification.ActivityInternalStatus.NOTICED.index);
        activityMybatisDao.updateActivity(activityWithBLOBs);
    }

    /**
     * 参与活动
     * @param content
     */
    @Override
    public void joinActivity(String content,long uid) {
        Pattern pattern = Pattern.compile("(.*)(#.{0,128}#)(.*)");
        Matcher matcher = pattern.matcher(content);
        boolean result = matcher.matches();
        if(result){
            String hashTitle = matcher.group(2);
            // 获取hash title
            ActivityWithBLOBs activityWithBLOBs = activityMybatisDao.getActivityByHashTitle(hashTitle);
            // 判断当前活动是否过期
            if(activityMybatisDao.isEnd(activityWithBLOBs.getId())) {
                activityWithBLOBs.setPersonTimes(activityWithBLOBs.getPersonTimes() + 1);
                activityMybatisDao.updateActivity(activityWithBLOBs);
                UserActivity userActivity = new UserActivity();
                userActivity.setActivityId(activityWithBLOBs.getId());
                userActivity.setUid(uid);
                activityMybatisDao.createUserActivity(userActivity);
            }
        }
    }

    @Override
    public ActivityH5Dto getActivityH5(long id) {
        ActivityH5Dto activityH5Dto = new ActivityH5Dto();
        ActivityWithBLOBs activityWithBLOBs = activityMybatisDao.getActivityById(id);
        if(activityWithBLOBs == null){
            return null;
        }
        int internalStatus = activityWithBLOBs.getInternalStatus();
        if(internalStatus == Specification.ActivityInternalStatus.NOTICED.index){
            activityH5Dto.setActivityContent(activityWithBLOBs.getActivityResult());
            activityH5Dto.setTitle(activityWithBLOBs.getActivityNoticeTitle());
            activityH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + activityWithBLOBs.getActivityNoticeCover());
        }else {
            activityH5Dto.setActivityContent(activityWithBLOBs.getActivityContent());
            activityH5Dto.setTitle(activityWithBLOBs.getActivityTitle());
            activityH5Dto.setCoverImage(Constant.QINIU_DOMAIN + "/" + activityWithBLOBs.getActivityCover());
        }
        UserProfile userProfile = userService.getUserProfileByUid(activityWithBLOBs.getUid());
        activityH5Dto.setNickName(userProfile.getNickName());
        activityH5Dto.setPublishTime(activityWithBLOBs.getCreateTime());
        activityH5Dto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        return activityH5Dto;
    }

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("(.*)(#.{0,128}#)(.*)");
        Matcher matcher = pattern.matcher("#中国人#");
        boolean v = matcher.matches();
        System.out.println(v);
        int i = matcher.groupCount();
        System.out.println(i);
        String value = matcher.group(2);
        System.out.println(value);
    }
}
