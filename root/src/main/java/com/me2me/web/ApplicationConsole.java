package com.me2me.web;

import com.me2me.activity.dto.CreateActivityDto;
import com.me2me.activity.dto.CreateActivityNoticeDto;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.web.Request;
import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.EditorContentDto;
import com.me2me.content.dto.KingTopicDto;
import com.me2me.content.service.ContentService;
import com.me2me.user.dto.*;
import com.me2me.user.service.UserService;
import com.me2me.web.request.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/console")
public class ApplicationConsole extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ActivityService activityService;

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/bindAccount",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response bindAccount(BindAccountRequest bindAccountRequest){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile(CommonUtils.getRandom("8",10));
        userSignUpDto.setGender(0);
        userSignUpDto.setEncrypt("123456");
        userSignUpDto.setNickName(bindAccountRequest.getNickName());
        return userService.signUp(userSignUpDto);
    }

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/showContents",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showContents(ShowContentsRequest showContentsRequest){
        EditorContentDto editorContentDto = new EditorContentDto();
        editorContentDto.setArticleType(showContentsRequest.getArticleType());
        editorContentDto.setPage(showContentsRequest.getPage());
        editorContentDto.setPageSize(showContentsRequest.getPageSize());
        editorContentDto.setKeyword(showContentsRequest.getKeyword());
        return contentService.showContents(editorContentDto);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 运营中心创建活动
     * @return
     */
    @RequestMapping(value = "/createActivity",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response createActivity(CreateActivityRequest request){
        CreateActivityDto createActivityDto = new CreateActivityDto();
        createActivityDto.setUid(request.getUid());
        createActivityDto.setIssue(request.getIssue());
        createActivityDto.setContent(request.getContent());
        createActivityDto.setCover(request.getCover());
        createActivityDto.setTitle(request.getTitle());
        createActivityDto.setHashTitle(request.getHashTitle());
        createActivityDto.setStartTime(request.getStartTime());
        createActivityDto.setEndTime(request.getEndTime());
        return activityService.createActivity(createActivityDto);
    }

    /**
     * 运营中心创建活动
     * @return
     */
    @RequestMapping(value = "/createActivityNotice",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response createActivityNotice(CreateActivityNoticeRequest request){
        CreateActivityNoticeDto createActivityNoticeDto = new CreateActivityNoticeDto();
        createActivityNoticeDto.setId(request.getId());
        createActivityNoticeDto.setActivityNoticeCover(request.getActivityNoticeCover());
        createActivityNoticeDto.setActivityResult(request.getActivityResult());
        createActivityNoticeDto.setActivityNoticeTitle(request.getActivityNoticeTitle());
        activityService.createActivityNotice(createActivityNoticeDto);
        return Response.success();
    }

    /**
     * 运营中心活动列表
     * @return
     */
    @RequestMapping(value = "/showActivity",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showActivity(ShowActivityRequest request){
        return activityService.showActivity(request.getPage(),request.getPageSize(),request.getKeyword());
    }

    /**
     * 运营操作接口
     * @return
     */
    @RequestMapping(value = "/option",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response option(OptionRequest request){
        return contentService.option(request.getId(),request.getOptionAction(),request.getAction());
    }

    /**
     * 运营操作接口
     * @return
     */
    @RequestMapping(value = "/showDetails",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showDetails(ShowDetailsRequest request){
        return contentService.showUGCDetails(request.getId());
    }

    /**
     * 运营操作接口
     * @return
     */
    @RequestMapping(value = "/modify",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response modify(PGCModifyRequest request){
        ContentDto contentDto = new ContentDto();
        contentDto.setUid(request.getUid());
        contentDto.setId(request.getId());
        contentDto.setContent(request.getContent());
        contentDto.setFeeling(request.getFeeling());
        contentDto.setContentType(request.getContentType());
        contentDto.setImageUrls(request.getImageUrls());
        contentDto.setType(request.getType());
        contentDto.setTitle(request.getTitle());
        contentDto.setRights(request.getRights());
        contentDto.setCoverImage(request.getCoverImage());
        contentDto.setForwardCid(request.getForwardCid());
        contentDto.setForWardUrl(request.getForwardUrl());
        contentDto.setForwardTitle(request.getForwardTitle());
        contentDto.setIsTop(request.getIsTop());
        contentDto.setAction(request.getAction());
        return contentService.modifyPGC(contentDto);
    }

    /**
     * 国王直播相关数据
     * @param request
     * @return
     */
    @RequestMapping(value = "/kingTopic",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response kingTopic(TopicCountRequest request){
        KingTopicDto kingTopic = new KingTopicDto();
        kingTopic.setUid(request.getKingUid());
        kingTopic.setReviewCount(request.getReviewCount());
        kingTopic.setLikeCount(request.getLikeCount());
        kingTopic.setEndDate(request.getEndDate());
        kingTopic.setStartDate(request.getStartDate());
        kingTopic.setNickName(request.getNickName());
        return contentService.kingTopic(kingTopic);

    }

    /**
     * 国王直播相关数据
     * @param request
     * @return
     */
    @RequestMapping(value = "/promoter",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response promoter(PromoterRequest request){
        return userService.getPromoter(request.getNickName(),request.getStartDate(),request.getEndDate());
    }

    @RequestMapping(value = "/getPhoto",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getPhoto(GetLivesRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Long.MAX_VALUE);
        }
        return userService.getPhoto(request.getSinceId());
    }

    @RequestMapping(value = "/initNameGroup",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response initNameGroup(){
    	userService.initNameGroup();
    	return Response.success();
    }
}
