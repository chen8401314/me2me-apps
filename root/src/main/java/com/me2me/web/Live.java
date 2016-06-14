package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.service.LiveService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/11
 * Time :18:09
 */
@Controller
@RequestMapping(value = "/api/live")
public class Live extends BaseController {

    @Autowired
    private LiveService liveService;

    /**
     * 创建直接
     * @return
     */
    @RequestMapping(value = "/createLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response createLive(CreateLiveRequest request){
        CreateLiveDto createLiveDto = new CreateLiveDto();
        createLiveDto.setUid(request.getUid());
        createLiveDto.setTitle(request.getTitle());
        createLiveDto.setLiveImage(request.getLiveImage());
        return liveService.createLive(createLiveDto);
    }

    /**
     * 获取消息列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/liveTimeline",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response liveTimeline(LiveTimelineRequest request){
        GetLiveTimeLineDto getLiveTimeLineDto = new GetLiveTimeLineDto();
        getLiveTimeLineDto.setSinceId(request.getSinceId());
        getLiveTimeLineDto.setTopicId(request.getTopicId());
        getLiveTimeLineDto.setUid(request.getUid());
        return liveService.getLiveTimeline(getLiveTimeLineDto);
    }

    /**
     * 获取消息列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/liveTimelineBarrage",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response liveTimelineBarrage(LiveTimelineRequest request){
        GetLiveTimeLineDto getLiveTimeLineDto = new GetLiveTimeLineDto();
        getLiveTimeLineDto.setSinceId(request.getSinceId());
        getLiveTimeLineDto.setTopicId(request.getTopicId());
        getLiveTimeLineDto.setUid(request.getUid());
        return liveService.liveTimelineBarrage(getLiveTimeLineDto);
    }

    /**
     * 获取消息列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/timeline",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response timeline(LiveTimelineRequest request){
        GetLiveTimeLineDto getLiveTimeLineDto = new GetLiveTimeLineDto();
        getLiveTimeLineDto.setSinceId(request.getSinceId());
        getLiveTimeLineDto.setTopicId(request.getTopicId());
        getLiveTimeLineDto.setUid(request.getUid());
        return liveService.liveTimeline(getLiveTimeLineDto);
    }

    /**
     * 直播发话
     * @param request
     * @return
     */
    @RequestMapping(value = "/speak",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response speak(SpeakRequest request){
        SpeakDto speakDto = new SpeakDto();
        speakDto.setType(request.getType());
        speakDto.setContentType(request.getContentType());
        speakDto.setFragment(request.getFragment());
        speakDto.setFragmentImage(request.getFragmentImage());
        speakDto.setUid(request.getUid());
        speakDto.setTopicId(request.getTopicId());
        speakDto.setTopId(request.getTopId());
        speakDto.setBottomId(request.getBottomId());
        return liveService.speak(speakDto);
    }

    /**
     * 结束自己的直播
     * @param request
     * @return
     */
    @RequestMapping(value = "/finishMyLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response finishMyLive(FinishMyLiveRequest request){
        return liveService.finishMyLive(request.getUid(),request.getTopicId());
    }


    /**
     *  关注，取消关注
     * @param request
     * @return
     */
    @RequestMapping(value = "/setLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response setLive(SetLiveRequest request){
        return liveService.setLive(request.getUid(),request.getTopicId());
    }

    /**
     *  获取所有正在直播列表(废弃)
     * @param request
     * @return
     */
   /* @RequestMapping(value = "/getLives",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLives(GetLivesRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Long.MAX_VALUE);
        }
        return liveService.getLives(request.getUid(),request.getSinceId());
    }*/


    @RequestMapping(value = "/getLives",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLives(GetLivesRequest request){
        long updateTime = request.getUpdateTime();
        if(updateTime==0){
            request.setUpdateTime2(new Date());
        }else{
            request.setUpdateTime2(new Date(updateTime));
        }
        return liveService.getLives(request.getUid(),request.getUpdateTime2());
    }

//    @InitBinder
//    public void initBinder(DataBinder binder) {
//        binder.registerCustomEditor(GetLivesRequest.class,"updateTime",new LiveUpdateTimePropertyEditor());
//    }

    /**
     * 获取我关注和我自己的直播列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMyLives",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getMyLives(GetMyLivesRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Long.MAX_VALUE);
        }
        return liveService.getMyLives(request.getUid(),request.getSinceId());
    }


    /**
     * 完结的直播移除
     * @param request
     * @return
     */
    @RequestMapping(value = "/removeLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response removeLive(RemoveLiveRequest request){
        return liveService.removeLive(request.getUid(),request.getTopicId());
    }

    /**
     * 退出直播
     * @param request
     * @return
     */
    @RequestMapping(value = "/signOutLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response signOutLive(SignOutLiveRequest request){
        return liveService.signOutLive(request.getUid(),request.getTopicId());
    }

    /**
     * 退出直播
     * @param request
     * @return
     */
    @RequestMapping(value = "/favoriteList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response favoriteList(FavoriteListRequest request){
        return liveService.getFavoriteList(request.getTopicId());
    }

    /**
     * 直播封面接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/liveCover",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response favoriteList(LiveCoverRequest request){
        return liveService.liveCover(request.getTopicId());
    }



}
