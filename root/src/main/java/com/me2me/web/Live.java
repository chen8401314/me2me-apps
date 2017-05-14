package com.me2me.web;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

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
import com.me2me.live.service.LiveService;
import com.me2me.web.request.AggregationOptRequest;
import com.me2me.web.request.AggregationPublishRequest;
import com.me2me.web.request.BarrageRequest;
import com.me2me.web.request.CreateKingdomRequest;
import com.me2me.web.request.CreateLiveRequest;
import com.me2me.web.request.CreateVoteRequest;
import com.me2me.web.request.DeleteLiveFragmentRequest;
import com.me2me.web.request.DisplayProtocolRequest;
import com.me2me.web.request.DropAroundRequest;
import com.me2me.web.request.EditSpeakRequest;
import com.me2me.web.request.FavoriteListRequest;
import com.me2me.web.request.FinishMyLiveRequest;
import com.me2me.web.request.FragmentForwardRequest;
import com.me2me.web.request.GetLiveByCidRequest;
import com.me2me.web.request.GetLivesRequest;
import com.me2me.web.request.GetMyLivesRequest;
import com.me2me.web.request.ImgDBRequest;
import com.me2me.web.request.InactiveLiveRequest;
import com.me2me.web.request.KingdomSearchRequest;
import com.me2me.web.request.LiveCoverRequest;
import com.me2me.web.request.LiveDetailRequest;
import com.me2me.web.request.LiveQrcodeRequest;
import com.me2me.web.request.LiveTimeline2Request;
import com.me2me.web.request.LiveTimelineRequest;
import com.me2me.web.request.LiveUpdateRequest;
import com.me2me.web.request.RecQueryRequest;
import com.me2me.web.request.RemoveLiveRequest;
import com.me2me.web.request.RemoveTopicRequest;
import com.me2me.web.request.ResendVoteRequest;
import com.me2me.web.request.SetLiveRequest;
import com.me2me.web.request.SettingModifyRequest;
import com.me2me.web.request.SignOutLiveRequest;
import com.me2me.web.request.SpeakRequest;
import com.me2me.web.request.TagKingdomsRequest;
import com.me2me.web.request.TestLiveRequest;
import com.me2me.web.request.TopicOptRequest;
import com.me2me.web.request.TopicRecommRequest;
import com.me2me.web.request.TopicTagCheckRequest;
import com.me2me.web.request.TopicTagsModifyRequest;
import com.me2me.web.request.TopicTagsRequest;
import com.me2me.web.request.TopicVoteInfoRequest;
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
        if(VersionUtil.isNewVersion(version, "2.2.5")){
        	liveDetailDto.setVersionFlag(3);
        }else if(VersionUtil.isNewVersion(version, "2.2.4")){
        	liveDetailDto.setVersionFlag(2);
        }else if(VersionUtil.isNewVersion(version, "2.2.2")){//222版本的限制
        	liveDetailDto.setVersionFlag(1);
        }
        
        return liveService.getLiveDetail(liveDetailDto);
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
        return liveService.liveCover(request.getTopicId(),request.getUid(),vflag);
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
    
}
