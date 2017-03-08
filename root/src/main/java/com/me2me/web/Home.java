package com.me2me.web;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.content.service.ContentService;
import com.me2me.web.request.*;
import com.me2me.web.utils.VersionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/home")
public class Home extends BaseController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ActivityService activityService;

    /**
     * 最热（小编发布，活动轮播位）
     * @return
     */
    @RequestMapping(value = "/hottest",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response hottest(HottestRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.getHottest(request.getSinceId(),request.getUid());
    }

    /**
     * 最热（小编发布，活动轮播位,最热按照上热点事件排序）
     * @return
     */
    @RequestMapping(value = "/hottest2",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response hottest2(HottestRequest request, HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "*");
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        int flag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.0")){
        	flag = 1;
        }
        return contentService.Hottest2(request.getSinceId(),request.getUid(), flag);
    }

    /**
     * 活动列表
     * @return
     */
    @RequestMapping(value = "/activity",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response activity(ActivityRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
       return activityService.getActivity(request.getSinceId(),request.getUid());
    }



    /**
     * 专属（老徐那边的数据接口）
     * 兼容2.0.3版本使用
     * @return
     */
    @RequestMapping(value = "/special",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response special(SpecialRequest request){
        return contentService.recommend(request.getUid(),request.getEmotion());
    }

    /**
     * 用户日记，直播
     * @param request
     * @return
     */
    @RequestMapping(value = "/newest",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response newest(NewestRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.0")){
        	vflag = 1;
        }
        return contentService.Newest(request.getSinceId(),request.getUid(), vflag);
    }

    /**
     * 关注（我关注的人，包含直播和ugc）
     * @param request
     * @return
     */
    @RequestMapping(value = "/attention ",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response attention (AttentionRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.0")){
        	vflag = 1;
        }
        return contentService.Attention(request.getSinceId(),request.getUid(),vflag);
    }

    /**
     * 新热点接口
     * V2.2.1版本开始使用本接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/hotList ",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response hotList(HotListRequest request){
    	return contentService.hotList(request.getSinceId(), request.getUid());
    }

    /**
     * 更多热点聚合王国列表接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/ceKingdomHotList ",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response ceKingdomHotList(CeKingdomHotListRequest request){
    	return contentService.ceKingdomHotList(request.getSinceId(), request.getUid());
    }
}
