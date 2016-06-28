package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.LiveBarrageDto;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.service.LiveService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Calendar;

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
     * 获取弹幕息列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/barrage",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response barrage(BarrageRequest request){
        LiveBarrageDto liveBarrageDto = new LiveBarrageDto();
        liveBarrageDto.setSinceId(request.getSinceId());
        liveBarrageDto.setTopicId(request.getTopicId());
        liveBarrageDto.setUid(request.getUid());
        liveBarrageDto.setTopId(request.getTopId());
        liveBarrageDto.setBottomId(request.getBottomId());
        return liveService.barrage(liveBarrageDto);
    }

    /**
     * 获取消息列表(不用)
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
        return liveService.setLive(request.getUid(),request.getTopicId(),request.getTopId(),request.getBottomId());
    }

    /**
     * 获取所有正在直播列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLives",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLives(GetLivesRequest request){
        if(request.getUpdateTime() == 0){
            Calendar calendar = Calendar.getInstance();
            request.setUpdateTime(calendar.getTimeInMillis());
        }
        return liveService.getLivesByUpdateTime(request.getUid(),request.getUpdateTime());
    }

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
     * 获取我关注和我自己的直播列表 (按主播最后的更新时间)
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMyLivesByUpdateTime",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getMyLivesByUpdateTime(GetMyLivesRequest request){
        return liveService.getMyLivesByUpdateTime(request.getUid(),request.getUpdateTime());
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
     * 订阅的直播列表
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


    /**
     * 根据cid获取直播信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLiveByCid",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLiveByCid(GetLiveByCidRequest request){
        return liveService.getLiveByCid(request.getCid(),request.getUid());
    }

    /**
     * 获取三天之前的直播列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/inactiveLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getInactiveLive(InactiveLiveRequest request){
        return liveService.getInactiveLive(request.getUid(),request.getUpdateTime());
    }



}
