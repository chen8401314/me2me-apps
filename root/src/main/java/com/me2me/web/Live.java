package com.me2me.web;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.common.utils.CommonUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.kafka.service.KafkaService;
import com.me2me.live.dto.AggregationOptDto;
import com.me2me.live.dto.CreateKingdomDto;
import com.me2me.live.dto.CreateLiveDto;
import com.me2me.live.dto.CreateVoteDto;
import com.me2me.live.dto.GetLiveDetailDto;
import com.me2me.live.dto.GetLiveTimeLineDto;
import com.me2me.live.dto.GetLiveTimeLineDto2;
import com.me2me.live.dto.GetLiveUpdateDto;
import com.me2me.live.dto.KingdomSearchDTO;
import com.me2me.live.dto.LiveBarrageDto;
import com.me2me.live.dto.SettingModifyDto;
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.dto.TestApiDto;
import com.me2me.live.dto.UserAtListDTO;
import com.me2me.live.model.LotteryInfo;
import com.me2me.live.service.LiveService;
import com.me2me.search.service.SearchService;
import com.me2me.sms.service.SmsService;
import com.me2me.web.request.AggregationOptRequest;
import com.me2me.web.request.AggregationPublishRequest;
import com.me2me.web.request.BarrageRequest;
import com.me2me.web.request.CreateKingdomRequest;
import com.me2me.web.request.CreateLiveRequest;
import com.me2me.web.request.CreateLotteryRequest;
import com.me2me.web.request.CreateVoteRequest;
import com.me2me.web.request.DaySignInfoRequest;
import com.me2me.web.request.DelLotteryContentRequest;
import com.me2me.web.request.DeleteLiveFragmentRequest;
import com.me2me.web.request.DetailFidPageRequest;
import com.me2me.web.request.DetailPageStatusRequest;
import com.me2me.web.request.DisplayProtocolRequest;
import com.me2me.web.request.DropAroundRequest;
import com.me2me.web.request.EditLotteryRequest;
import com.me2me.web.request.EditSpeakRequest;
import com.me2me.web.request.FavoriteListRequest;
import com.me2me.web.request.FinishMyLiveRequest;
import com.me2me.web.request.FragmentForwardRequest;
import com.me2me.web.request.GetJoinLotteryUsersRequest;
import com.me2me.web.request.GetKingdomPriceRequest;
import com.me2me.web.request.GetKingdomTransferRecordRequest;
import com.me2me.web.request.GetLiveByCidRequest;
import com.me2me.web.request.GetLivesRequest;
import com.me2me.web.request.GetLotteryListRequest;
import com.me2me.web.request.GetLotteryRequest;
import com.me2me.web.request.GetMyLivesRequest;
import com.me2me.web.request.GivenKingdomRequest;
import com.me2me.web.request.ImgDBRequest;
import com.me2me.web.request.InactiveLiveRequest;
import com.me2me.web.request.JoinLotteryRequest;
import com.me2me.web.request.KingdomSearchRequest;
import com.me2me.web.request.ListTopicListedRequest;
import com.me2me.web.request.ListTopicRequest;
import com.me2me.web.request.LiveCoverRequest;
import com.me2me.web.request.LiveDetailRequest;
import com.me2me.web.request.LiveQrcodeRequest;
import com.me2me.web.request.LiveTimeline2Request;
import com.me2me.web.request.LiveTimelineRequest;
import com.me2me.web.request.LiveUpdateRequest;
import com.me2me.web.request.ProhibitLotteryRequest;
import com.me2me.web.request.RecQueryRequest;
import com.me2me.web.request.RemoveLiveRequest;
import com.me2me.web.request.RemoveTopicRequest;
import com.me2me.web.request.ResendVoteRequest;
import com.me2me.web.request.RunLotteryRequest;
import com.me2me.web.request.SaveDaySignInfoRequest;
import com.me2me.web.request.SaveDaySignRecordRequest;
import com.me2me.web.request.SetLiveRequest;
import com.me2me.web.request.SettingModifyRequest;
import com.me2me.web.request.SignOutLiveRequest;
import com.me2me.web.request.SpeakRequest;
import com.me2me.web.request.StealKingdomCoinRequest;
import com.me2me.web.request.TagKingdomsRequest;
import com.me2me.web.request.TakeoverTopicRequest;
import com.me2me.web.request.TestLiveRequest;
import com.me2me.web.request.TopicOptRequest;
import com.me2me.web.request.TopicRecommRequest;
import com.me2me.web.request.TopicTagCheckRequest;
import com.me2me.web.request.TopicTagsModifyRequest;
import com.me2me.web.request.TopicTagsRequest;
import com.me2me.web.request.TopicVoteInfoRequest;
import com.me2me.web.request.UserAtListRequest;
import com.me2me.web.request.UserKingdomInfoRequest;
import com.me2me.web.request.VoteInfoRequest;
import com.me2me.web.request.VoteRequest;
import com.me2me.web.utils.VersionUtil;

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

    @Autowired
    private KafkaService kafkaService;
    
    @Autowired
    
    private SearchService searchService;
    
    @Autowired
    private SmsService smsService;
    
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
    
    @RequestMapping(value = "/createKingdom",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response createKingdom(CreateKingdomRequest request){
    	CreateKingdomDto dto = new CreateKingdomDto();
    	dto.setCExtra(request.getCExtra());
    	dto.setContentType(request.getContentType());
    	dto.setExtra(request.getExtra());
    	dto.setFragment(request.getFragment());
    	dto.setKType(request.getKType());
    	dto.setLiveImage(request.getLiveImage());
    	dto.setSource(request.getSource());
    	dto.setTitle(request.getTitle());
    	dto.setUid(request.getUid());
    	dto.setKConfig(request.getKConfig());
    	dto.setTags(request.getTags());
    	dto.setAutoTags(request.getAutoTags());
    	return liveService.createKingdom(dto);
    }

    /**
     * 获取消息列表
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/liveTimeline",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response liveTimeline(LiveTimelineRequest request){
        GetLiveTimeLineDto getLiveTimeLineDto = new GetLiveTimeLineDto();
        getLiveTimeLineDto.setSinceId(request.getSinceId());
        getLiveTimeLineDto.setTopicId(request.getTopicId());
        getLiveTimeLineDto.setUid(request.getUid());
        getLiveTimeLineDto.setVersion(request.getVersion());
        if(request.getPageSize() <= 0){
        	getLiveTimeLineDto.setPageSize(50);
        }else{
        	getLiveTimeLineDto.setPageSize(request.getPageSize());
        }
        
        return liveService.getLiveTimeline(getLiveTimeLineDto);
    }

    /**
     * 获取王国消息列表，按分页
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/detail",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLiveDetail(LiveDetailRequest request){
        GetLiveDetailDto liveDetailDto = new GetLiveDetailDto();
        liveDetailDto.setTopicId(request.getTopicId());
        int offset = request.getOffset()==0?50:request.getOffset();
        int pageNo = request.getPageNo()==0?1:request.getPageNo();
        liveDetailDto.setOffset(offset);
        liveDetailDto.setPageNo(pageNo);
        liveDetailDto.setUid(request.getUid());
        liveDetailDto.setSinceId(request.getSinceId());
        liveDetailDto.setDirection(request.getDirection());
        liveDetailDto.setVersionFlag(0);
        
        String version = request.getVersion();
        if(VersionUtil.isNewVersion(version, "3.0.1")){
        	liveDetailDto.setVersionFlag(4);
        }else if(VersionUtil.isNewVersion(version, "2.2.5")){
        	liveDetailDto.setVersionFlag(3);
        }else if(VersionUtil.isNewVersion(version, "2.2.4")){
        	liveDetailDto.setVersionFlag(2);
        }else if(VersionUtil.isNewVersion(version, "2.2.2")){//222版本的限制
        	liveDetailDto.setVersionFlag(1);
        }
        
        return liveService.getLiveDetail(liveDetailDto);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/detailPageStatus",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response detailPageStatus(DetailPageStatusRequest request){
    	return liveService.detailPageStatus(request.getTopicId(), request.getPageNo(), request.getOffset());
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/detailFidPage",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response detailFidPage(DetailFidPageRequest request){
    	return liveService.detailFidPage(request.getTopicId(), request.getFid(), request.getOffset());
    }
    
    /**
     * 王国内容更新数量接口（配合王国详情接口）
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getUpdate",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getUpdate(LiveUpdateRequest request){
        GetLiveUpdateDto getLiveUpdateDto = new GetLiveUpdateDto();
        int offset = request.getOffset()==0?50:request.getOffset();
        getLiveUpdateDto.setOffset(offset);
        getLiveUpdateDto.setTopicId(request.getTopicId());
        getLiveUpdateDto.setSinceId(request.getSinceId());
        return liveService.getLiveUpdate(getLiveUpdateDto);
    }




    /**
     * 获取消息列表(暂未启用)
     * @param request
     * @return
     */
    @RequestMapping(value = "/liveTimeline2",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response liveTimeline2(LiveTimeline2Request request){
        GetLiveTimeLineDto2 getLiveTimeLineDto = new GetLiveTimeLineDto2();
        getLiveTimeLineDto.setSinceId(request.getSinceId());
        getLiveTimeLineDto.setTopicId(request.getTopicId());
        getLiveTimeLineDto.setUid(request.getUid());
        getLiveTimeLineDto.setMode(request.getMode());
        getLiveTimeLineDto.setDirection(request.getDirection());
        getLiveTimeLineDto.setFirst(request.getFirst());
        getLiveTimeLineDto.setForms(request.getForms());
        return liveService.getLiveTimeline2(getLiveTimeLineDto);
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
        getLiveTimeLineDto.setVersion(request.getVersion());
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
        speakDto.setAtUid(request.getAtUid());
        speakDto.setMode(request.getMode());
        speakDto.setSource(request.getSource());
        speakDto.setExtra(request.getExtra());

//        try {  //埋点
//            ClientLog clientLog = new ClientLog();
//
//            clientLog.setAction(Specification.ClientLogAction.LIVE_LIKES.index);
//            clientLog.setExt(Specification.ClientLogAction.LIVE_LIKES.name+":"+request.getType());
//            clientLog.setUserId(request.getUid());
//            clientLog.setChannel(request.getChannel());
//            clientLog.setVersion(request.getVersion());
//            clientLog.setUserAgent(req.getHeader("User-Agent"));
//
//            kafkaService.clientLog(clientLog);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        return liveService.speak(speakDto);
    }

    /**
     * 修改发言内容（暂时只更新extra字段）
     * @param request
     * @return
     */
    @RequestMapping(value = "/editSpeak",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response editSpeak(EditSpeakRequest request){
        SpeakDto speakDto = new SpeakDto();
        speakDto.setFragmentId(request.getFragmentId());
        speakDto.setExtra(request.getExtra());

        return liveService.editSpeak(speakDto);
    }
    /**
     * 结束自己的直播
     * @param request
     * @return
     */
    @RequestMapping(value = "/finishMyLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response finishMyLive(FinishMyLiveRequest request){
    	return Response.success(ResponseStatus.USER_FINISH_LIVE_SUCCESS.status, ResponseStatus.USER_FINISH_LIVE_SUCCESS.message);
//        return liveService.finishMyLive(request.getUid(),request.getTopicId());
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
     *  关注，取消关注
     * @param request
     * @return
     */
    @RequestMapping(value = "/setLive2",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response setLive2(SetLiveRequest request){
        return liveService.setLive2(request.getUid(),request.getTopicId(),request.getTopId(),request.getBottomId(),request.getAction());
    }

    /**
     * 获取所有正在直播列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLives",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLives(GetLivesRequest request, HttpServletRequest req){
        if(request.getUpdateTime() == 0){
            Calendar calendar = Calendar.getInstance();
            request.setUpdateTime(calendar.getTimeInMillis());
        }

        //埋点
//        kafkaService.saveClientLog(request,req.getHeader("User-Agent"), Specification.ClientLogAction.LIVE_IN_UPDATE);
        return liveService.LivesByUpdateTime(request.getUid(),request.getUpdateTime());
    }

    /**
     * 获取我关注和我自己的直播列表(老版本)
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMyLives",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getMyLives(GetMyLivesRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Long.MAX_VALUE);
        }
        return liveService.MyLives(request.getUid(),request.getSinceId());
    }

    /**
     * 获取我关注和我自己的直播列表 (按主播最后的更新时间)
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMyLivesByUpdateTime",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getMyLivesByUpdateTime(GetMyLivesRequest request){
    	String version = request.getVersion();
    	if(VersionUtil.isNewVersion(version, "2.2.0")){//2.2.0版本，不需要3天未更新的的合集了，都在列表里展现
    		return liveService.myLivesAllByUpdateTime(request.getUid(),request.getUpdateTime());
    	}
    	
        return liveService.MyLivesByUpdateTime(request.getUid(),request.getUpdateTime());
    }

    /**
     * 王国互动接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMyTopic",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getMyTopic(GetMyLivesRequest request){
        return liveService.getMyTopic(request.getUid(),request.getUpdateTime());
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
     * 直播封面接口（调用时候直播阅读数+1）
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/liveCover",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response liveCover(LiveCoverRequest request){
        int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.3")){
        	vflag = 1;
        }
        return liveService.liveCover(request.getTopicId(),request.getUid(),vflag, request.getSource());
    }


    /**
     * 根据cid获取直播信息
     * @param request
     * @return
     */
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/getLiveByCid",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getLiveByCid(GetLiveByCidRequest request){
        int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.3")){
        	vflag = 1;
        }
        return liveService.getLiveByCid(request.getCid(),request.getUid(),vflag);
    }

    /**
     * 获取三天之前的直播列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/inactiveLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getInactiveLive(InactiveLiveRequest request){
        //埋点
//        kafkaService.saveClientLog(request,req.getHeader("User-Agent"), Specification.ClientLogAction.LIVE_NOT_UPDATED);

        return liveService.getInactiveLive(request.getUid(),request.getUpdateTime());
    }

    /**
     * 获取三天之前的直播列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/cleanUpdate",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response cleanUpdate(InactiveLiveRequest request){
        return liveService.cleanUpdate(request.getUid());
    }


    /**
     * 获取直播详情的二维码
     * @param request
     * @return
     */
    @RequestMapping(value = "/liveQrcode",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response liveQrcode(LiveQrcodeRequest request){
        return liveService.genQRcode(request.getTopicId());
    }


    /**
     * 删除王国跟帖内容
     * @param request
     * @return
     */
    @RequestMapping(value = "/delLiveFragment",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delLiveFragment(DeleteLiveFragmentRequest request){
        return liveService.deleteLiveFragment(request.getTopicId(),request.getFid(),request.getUid());
    }

    /**
     * 获取显示协议
     * @param request
     * @return
     */
    @RequestMapping(value = "/displayProtocol",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response displayProtocol(DisplayProtocolRequest request){
        return liveService.displayProtocol(request.getVLv());
    }

    /**
     * 打开app调用此接口获取王国更新红点(未启用)
     */
    @ResponseBody
    @RequestMapping(value = "/getRedDot",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getRedDot(GetMyLivesRequest request){
        return liveService.getRedDot(request.getUid(),request.getUpdateTime());
    }

    /**
     * 测试接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/testApi",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response testApi(TestLiveRequest request){
        TestApiDto dto = new TestApiDto();
        System.out.println("请求了一次");
        try {
            BeanUtils.copyProperties(dto,request);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return liveService.testApi(dto);
    }
    
    /**
     * 王国检索接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/kingdomSearch",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response kingdomSearch(KingdomSearchRequest request){
    	KingdomSearchDTO dto = new KingdomSearchDTO();
    	dto.setAllowCore(request.getAllowCore());
    	dto.setExceptTopicId(request.getExceptTopicId());
    	dto.setKeyword(request.getKeyword());
    	dto.setSearchRights(request.getSearchRights());
    	dto.setSearchType(request.getSearchType());
    	dto.setSearchUid(request.getSearchUid());
    	dto.setTopicId(request.getTopicId());
    	dto.setTopicType(request.getTopicType());
    	dto.setUpdateTime(request.getUpdateTime());
    	dto.setSearchScene(request.getSearchScene());
    	
    	int vflag = 0;
    	if(VersionUtil.isNewVersion(request.getVersion(), "2.2.3")){
        	vflag = 2;
        }else if(VersionUtil.isNewVersion(request.getVersion(), "2.2.1")){
        	vflag = 1;
        }
    	
        dto.setVersionFlag(vflag);
        
    	return liveService.kingdomSearch(request.getUid(), dto);
    }

    /**
     * 王国设置信息查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/settings",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response settings(KingdomSearchRequest request){
    	int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.3")){
        	vflag = 1;
        }
        return liveService.settings(request.getUid() ,request.getTopicId(), vflag);
    }

    /**
     * 王国设置信息修改接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/settingModify",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response settingModify(SettingModifyRequest request){
        SettingModifyDto dto = new SettingModifyDto();
        CommonUtils.copyDto(request ,dto);
        return liveService.settingModify(dto);
    }

    /**
     * 聚合内容下发接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/aggregationPublish",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response aggregationPublish(AggregationPublishRequest request){
    	return liveService.aggregationPublish(request.getUid(), request.getTopicId(), request.getFid());
    }

    /**
     * 聚合操作接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/aggregationOpt",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response aggregationOpt(AggregationOptRequest request){
        AggregationOptDto dto = new AggregationOptDto();
        CommonUtils.copyDto(request ,dto);
        return liveService.aggregationOpt(dto);
    }

    /**
     * 聚合申请操作接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/aggregationApplyOpt",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response aggregationApplyOpt(AggregationOptRequest request){
        AggregationOptDto dto = new AggregationOptDto();
        CommonUtils.copyDto(request ,dto);
        return liveService.aggregationApplyOpt(dto);
    }
    
    /**
     * 王国内容转发接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/fragmentForward",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response fragmentForward(FragmentForwardRequest request){
    	return liveService.fragmentForward(request.getUid(), request.getFid(), request.getSourceTopicId(), request.getTargetTopicId());
    }

    /**
     * 王国推荐接口
     * @param recommRequest
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/recommend",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response recommend(TopicRecommRequest recommRequest){
        return liveService.recommend(recommRequest.getUid() ,recommRequest.getTopicId() ,recommRequest.getAction());
    }

    /**
     * 王国串门接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/dropAround",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response dropAround(DropAroundRequest request){
        return liveService.dropAround(request.getUid() ,request.getSourceTopicId());
    }

    /**
     * 王国互动操作接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/myTopicOpt",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response myTopicOpt(TopicOptRequest request){
        return liveService.myTopicOpt(request.getUid() ,request.getAction() ,request.getTopicId());
    }

    /**
     * 王国标签查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topicTags",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response topicTags(TopicTagsRequest request){
    	return liveService.topicTags(request.getUid(), request.getTopicId());
    }
    
    /**
     * 王国标签更新接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topicTagsModify",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response topicTagsModify(TopicTagsModifyRequest request){
    	return liveService.topicTagsModify(request.getUid(), request.getTopicId(), request.getTags());
    }
    
    /**
     * 王国标签校验接口
     * 校验是否被禁用
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/topicTagCheck",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response topicTagCheck(TopicTagCheckRequest request){
    	return liveService.topicTagCheck(request.getUid(), request.getTag());
    }
    
    /**
     * 标签王国查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/tagKingdoms",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response tagKingdoms(TagKingdomsRequest request){
    	return liveService.tagKingdoms(request.getTag(), request.getSinceId(), request.getUid());
    }
    
    /**
     * 王国关联推荐获取接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/recQuery",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response recQuery(RecQueryRequest request){
    	return liveService.recQuery(request.getTopicId(), request.getSinceId(), request.getUid());
    }
    
    /**
     * 逗一逗查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/teaseListQuery",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response teaseListQuery(){
    	return liveService.teaseListQuery();
    }
    /**
     * 投票创建接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/createVote",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response createVote(CreateVoteRequest request){
    	CreateVoteDto dto = new CreateVoteDto();
    	dto.setUid(request.getUid());
    	dto.setTopicId(request.getTopicId());
    	dto.setTitle(request.getTitle());
    	dto.setSource(request.getSource());
    	dto.setOption(request.getOption());
    	dto.setType(request.getType());
    	return liveService.createVote(dto);
    }
    /**
     * 投票接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/vote",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response vote(VoteRequest request){
    	return liveService.vote(request.getUid(), request.getVoteId(), request.getOptionId());
    }
    
    /**
     * 结束投票接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/endVote",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response endVote(VoteRequest request){
    	return liveService.endVote(request.getVoteId() ,request.getUid() );
    }
    
    /**
     * 投票重新发送接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/resendVote",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response resendVote(ResendVoteRequest request){
    	return liveService.resendVote(request.getFragmentId(),request.getUid());
    }
    
    /**
     * 王国详情列表投票信息查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getTopicVoteInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getTopicVoteInfo(TopicVoteInfoRequest request){
    	return liveService.getTopicVoteInfo(request.getVoteId());
    }
    
    /**
     * 投票详情查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getVoteInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getVoteInfo(VoteInfoRequest request){
    	return liveService.getVoteInfo(request.getVoteId(),request.getUid());
    }
    /**
     * 图库接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/kingdomImgDB",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response recQuery(ImgDBRequest request){
    	Response resp= liveService.kingdomImgDB(request.getTopicId(), request.getDirection(), request.getFid(),request.getType());
    	return resp;
    }
    /**
     * 移除王国
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeKingdom",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response removeKingdom(RemoveTopicRequest request){
    	Response resp= liveService.blockUserKingdom(request.getTopicId(),request.getUid());
    	return resp;
    }
    
    /**
     * 王国@用户列表接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userAtList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userAtList(UserAtListRequest request){
    	UserAtListDTO dto = new UserAtListDTO();
    	dto.setUid(request.getUid());
    	dto.setKeyword(request.getKeyword());
    	dto.setPage(request.getPage());
    	dto.setTopicId(request.getTopicId());
    	
    	return liveService.userAtList(dto);
    }
    
    /**
     * 王国转让历史查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getKingdomTransferRecord",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getKingdomTransferRecord(GetKingdomTransferRecordRequest request){
    	return liveService.getKingdomTransferRecord(request.getTopicId(),request.getSinceId());
    }
    /**
     * 偷金币
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/stealKingdomCoin",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response stealKingdomCoin(StealKingdomCoinRequest request){
    	
    	return liveService.stealKingdomCoin(request.getUid(),request.getTopicId());
    }
    /**
     * 王国价值详情获取接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getKingdomPrice",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getKingdomPrice(GetKingdomPriceRequest request){
    	return liveService.getKingdomPrice(request.getTopicId());
    }
    /**
     * 王国挂牌上市
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listTopic",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response listTopic(ListTopicRequest request){
    	return liveService.listTopic(request.getTopicId(),request.getUid());
    }
    /**
     * 上市王国列表查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listedTopicList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response listedTopicList(ListTopicListedRequest request){
    	return liveService.listedTopicList(request.getSinceId());
    }
    /**
     * 王国收购接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/takeoverTopic",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response takeoverTopic(TakeoverTopicRequest request){
    	return liveService.takeoverTopic(request.getTopicId(),request.getUid());
    }
    /**
     * 获取用户日签信息接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getDaySignInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getDaySignInfo(DaySignInfoRequest request){
    	return liveService.getDaySignInfo(request.getUid());
    }
    @ResponseBody
    @RequestMapping(value = "/givenKingdomOpration",method = RequestMethod.POST)
    public Response givenKingdom(GivenKingdomRequest request){
		return liveService.givenKingdomOpration(request.getUid(),request.getGivenKingdomId(),request.getAction());
    }
    /**
     * 保存日签信息接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveDaySignInfo",method = RequestMethod.POST)
    public Response saveDaySignInfo(SaveDaySignInfoRequest request){
		return liveService.saveDaySignInfo(request.getUid(), request.getImage(), request.getExtra(), request.getUids(), request.getSource() , request.getQuotationIds());
    }
    /**
     * 保存或分享日签图片记录接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveDaySignRecord",method = RequestMethod.POST)
    public Response saveDaySignRecord(SaveDaySignRecordRequest request){
		return liveService.saveSignSaveRecord(request.getUid(), request.getType());
    }
    
    /**
     * 用户王国信息查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userKingdomInfo",method = RequestMethod.POST)
    public Response userKingdomInfo(UserKingdomInfoRequest request){
    	return liveService.userKingdomInfo(request.getUid());
    }
    /**
     * 发起抽奖接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/createLottery",method = RequestMethod.POST)
    public Response createLottery(CreateLotteryRequest request){
    	LotteryInfo lotteryInfo  =new LotteryInfo();
    	lotteryInfo.setTopicId(request.getTopicId());
    	lotteryInfo.setTitle(request.getTitle());
    	lotteryInfo.setSummary(request.getSummary());
    	lotteryInfo.setWinNumber(request.getWinNumber());
    	lotteryInfo.setEndTime(new Date(request.getEndTime()));
    	lotteryInfo.setUid(request.getUid());
    	return liveService.createLottery(lotteryInfo, request.getSource());
    }
    /**
     * 编辑抽奖接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editLottery",method = RequestMethod.POST)
    public Response editLottery(EditLotteryRequest request){
    	LotteryInfo lotteryInfo  =new LotteryInfo();
    	lotteryInfo.setId(request.getLotteryId());
    	lotteryInfo.setTitle(request.getTitle());
    	lotteryInfo.setSummary(request.getSummary());
    	lotteryInfo.setWinNumber(request.getWinNumber());
    	lotteryInfo.setEndTime(new Date(request.getEndTime()));
    	lotteryInfo.setUid(request.getUid());
    	return liveService.editLottery(lotteryInfo);
    }
    /**
     * 获取抽奖详情接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getLottery",method = RequestMethod.POST)
    public Response getLottery(GetLotteryRequest request){
    	return liveService.getLottery(request.getLotteryId(),request.getUid());
    }
    /**
     * 抽奖参与用户查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getJoinLotteryUsers",method = RequestMethod.POST)
    public Response getJoinLotteryUsers(GetJoinLotteryUsersRequest request){
    	return liveService.getJoinLotteryUsers(request.getLotteryId(), request.getSinceId());
    }
    
    /**
     * 参与抽奖接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/joinLottery",method = RequestMethod.POST)
    public Response joinLottery(JoinLotteryRequest request){
    	return liveService.joinLottery(request.getLotteryId(), request.getContent(),request.getUid());
    }
    
    /**
     * 删除抽奖留言接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delLotteryContent",method = RequestMethod.POST)
    public Response delLotteryContent(DelLotteryContentRequest request){
    	return liveService.delLotteryContent(request.getContentId(),request.getUid());
    }
    
    /**
     * 屏蔽抽奖资格接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/prohibitLottery",method = RequestMethod.POST)
    public Response prohibitLottery(ProhibitLotteryRequest request){
    	return liveService.prohibitLottery(request.getLotteryId(),request.getUid(),request.getJoinUid());
    }
    
    /**
     * 获取抽奖列表接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getLotteryList",method = RequestMethod.POST)
    public Response getLotteryList(GetLotteryListRequest request){
    	return liveService.getLotteryList(request.getTopicId(), request.getSinceId(), request.getUid());
    }
    
    /**
     * 抽奖开奖接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/runLottery",method = RequestMethod.POST)
    public Response runLottery(RunLotteryRequest request){
    	return liveService.runLottery(request.getLotteryId(), request.getUid());
    }
}
