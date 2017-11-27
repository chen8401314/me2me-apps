package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.content.dto.*;
import com.me2me.content.service.ContentService;
import com.me2me.kafka.service.KafkaService;
import com.me2me.live.service.LiveService;
import com.me2me.user.service.UserService;
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
@RequestMapping(value = "/api/content")
public class Contents extends BaseController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private LiveService liveService;

    /**
     * 精选接口(已废)
     * @return
     */
    @RequestMapping(value = "/highQuality",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response highQuality(SquareRequest request){
        if(request.getSinceId()==-1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.highQuality(request.getSinceId(),request.getUid());
    }

    /**
     * 广场接口(已废)
     * @return
     */
    @RequestMapping(value = "/square",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response square(SquareRequest request){
        if(request.getSinceId()==-1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.square(request.getSinceId(),request.getUid());
    }

    /**
     * 用户发布接口
     * @return
     */
    @RequestMapping(value = "/publish",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response publish(PublishContentRequest request){
        ContentDto contentDto = new ContentDto();
        contentDto.setUid(request.getUid());
        contentDto.setContent(request.getContent());
        contentDto.setFeeling(request.getFeeling());
        contentDto.setContentType(request.getContentType());
        contentDto.setImageUrls(request.getImageUrls());
        contentDto.setImageHeights(request.getImageHeights());
        contentDto.setImageWidths(request.getImageWidths());
        contentDto.setType(request.getType());
        contentDto.setTitle(request.getTitle());
        contentDto.setRights(request.getRights());
        contentDto.setCoverImage(request.getCoverImage());
        contentDto.setForwardCid(request.getForwardCid());
        contentDto.setForWardUrl(request.getForwardUrl());
        contentDto.setForwardTitle(request.getForwardTitle());
        contentDto.setTargetTopicId(request.getTargetTopicId());
        
        return liveService.publishUGC(contentDto);
        
//        if(contentDto.getType() != 2) {
//            // 用户UGC入口
//            Response response = contentService.publish2(contentDto);
//            return response;
//        }else{
//            // 小编发布入口
//            return contentService.editorPublish(contentDto);
//        }
    }

    /**
     * 用户点赞接口
     * @return
     */
    @RequestMapping(value = "/likes",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response likes(LikeRequest request,HttpServletResponse req){
        LikeDto likeDto = new LikeDto();
        likeDto.setUid(request.getUid());
        likeDto.setCid(request.getCid());
        likeDto.setAction(request.getAction());
        //兼容老版本
        likeDto.setType(request.getType() == 0 ? 1 : request.getType());

        //埋点
//        kafkaService.saveClientLog(likeDto,req.getHeader("User-Agent"), Specification.ClientLogAction.UGC_LIKES);
        return contentService.like2(likeDto);
    }

    /**
     *  用户贴标签
     * @return
     */
    @RequestMapping(value = "/writeTag",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response writeTag(WriteTagRequest request,HttpServletResponse req){
        WriteTagDto writeTagDto = new WriteTagDto();
        writeTagDto.setCid(request.getCid());
        writeTagDto.setTag(request.getTag());
        writeTagDto.setUid(request.getUid());
        writeTagDto.setCustomerId(request.getCustomerId());
        //兼容老版本
        writeTagDto.setType(request.getType() == 0 ? 1 : request.getType());

        //埋点
//        kafkaService.saveClientLog(writeTagDto,req.getHeader("User-Agent"), Specification.ClientLogAction.UGC_FEEL);
        return contentService.writeTag2(writeTagDto);
    }

    /**
     *  用户删除发布内容
     * @return
     */
    @RequestMapping(value = "/deleteContent",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response deleteContent(DeleteContentRequest request){
        return contentService.deleteContent(request.getId(),request.getUid(),false);
    }

    /**
     * 用户发布列表接口(0为了兼容老版本/1非直播/2直播)
     * @return
     */
    @RequestMapping(value = "/myPublish",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response myPublish(MyPublishContentRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.0")){
        	vflag = 1;
        }
        return contentService.myPublish(request.getCustomerId(),request.getUpdateTime(),request.getType(),request.getSinceId(),request.getNewType(),vflag);
    }

    /**
     * 内容详情接口
     * @return
     */
    @RequestMapping(value = "/getContentDetail",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getContentDetail(ContentDetailRequest request, HttpServletResponse response ){
        response.setHeader("Access-Control-Allow-Origin", "*");
        return contentService.contentDetail(request.getId(),request.getUid());
    }

    /**
     * 用户资料卡
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getUserData(UserInfoRequest request){
        return contentService.UserData(request.getCustomerId(),request.getUid());
    }

    /**
     * 用户资料卡
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserData2",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getUserData2(UserInfoRequest request){
    	int vflag = 0;
    	if(VersionUtil.isNewVersion(request.getVersion(), "3.0.2")){
    		vflag = 2;
    	}else if(VersionUtil.isNewVersion(request.getVersion(), "2.2.0")){
        	vflag = 1;
        }
        return contentService.UserData2(request.getCustomerId(),request.getUid(),vflag);
    }

    /**
     * 小编精选(已废)
     * @param request
     * @return
     */
    @RequestMapping(value = "/selectedDate",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response selectedDate(SelectedDateRequest request){
        if(request.getSinceId()==-1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.SelectedData(request.getSinceId(),request.getUid());
    }

    /**
     * 精选首页(已废)
     * @param request
     * @return
     */
    @RequestMapping(value = "/highQualityIndex",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response highQualityIndex(SquareRequest request){
        if(request.getSinceId()==-1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.highQualityIndex(request.getSinceId(),request.getUid());
    }

    /**
     * 修改内容权限
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyRights",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response modifyRights(ModifyContentRequest request){

        return contentService.modifyRights(request.getRights(),request.getCid(),request.getUid());
    }

    /**
     * 活动列表接口（已废弃）
     * @param request
     * @return
     */
    @RequestMapping(value = "/activities",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response activities(ActivitiesRequest request){

        return contentService.Activities(request.getSinceId(),request.getUid());
    }

    /**
     * 文章评论接口
     * @return
     */
    @RequestMapping(value = "/review",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response review(ReviewRequest request,HttpServletResponse req){
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setUid(request.getUid());
        reviewDto.setCid(request.getCid());
        reviewDto.setReview(request.getReview());
        reviewDto.setIsAt(request.getIsAt());
        reviewDto.setAtUid(request.getAtUid());
        reviewDto.setExtra(request.getExtra());
        //兼容老版本
        reviewDto.setType(request.getType() == 0 ? 1 : request.getType());

        //埋点
//        kafkaService.saveClientLog(reviewDto,req.getHeader("User-Agent"), Specification.ClientLogAction.UGC_REVIEW);

        return contentService.createReview(reviewDto);
    }
    
    /**
     * 文章/UGC等评论删除接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/delReview",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response delReview(DelReviewRequest request){
    	ReviewDelDTO dto = new ReviewDelDTO();
    	dto.setUid(request.getUid());
    	dto.setCid(request.getCid());
    	dto.setRid(request.getRid());
    	dto.setType(request.getType());
    	
    	return contentService.deleteReview(dto);
    }

    /**
     * 文章评论列表
     * @return
     */
    @RequestMapping(value = "/reviewList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response reviewList(ReviewListRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.reviewList(request.getCid(),request.getSinceId(),request.getType());
    }

    /**
     * 老徐文章内容评论贴标点赞
     * @param request
     * @return
     */
    @RequestMapping(value = "/getArticleComments",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getArticleComments(ArticleCommentsRequest request){
        return Response.success();
    }

    /**
     * 用户日记列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/myPublishByType",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response myPublishByType(MyPublishContentRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        if(request.getUpdateTime() <= 0){
        	request.setUpdateTime(Long.MAX_VALUE);
        }
        int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "3.0.6")){
        	vflag = 3;
        }else if(VersionUtil.isNewVersion(request.getVersion(), "3.0.4")){
        	vflag = 2;
        }else if(VersionUtil.isNewVersion(request.getVersion(), "2.2.0")){
        	vflag = 1;
        }
        return contentService.myPublishByType(request.getCustomerId(),request.getSinceId(),request.getType(),request.getUpdateTime(),request.getUid(),vflag);
    }

    @RequestMapping(value = "/emojiPackageQuery",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response emojiPackageQuery(){
        return contentService.emojiPackageQuery();
    }
    
    @RequestMapping(value = "/emojiPackageDetail",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response emojiPackageDetail(EmojiPackageDetailRequest request){
    	return contentService.emojiPackageDetail(request.getPackageId());
    }
    
    @RequestMapping(value = "/shareRecord",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response shareRecord(ShareRecordRequest request){
    	return contentService.shareRecord(request.getUid(), request.getType(), request.getCid(), request.getShareAddr());
    }
    @RequestMapping(value = "/tagMgmtQuery",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response tagMgmtQuery(TagMgmtQueryRequest request){
    	return contentService.tagMgmtQuery(request.getType(), request.getPage(), request.getUid());
    }
    
    @RequestMapping(value = "/tagDetail",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response tagDetail(TagDetailRequest request){
    	return contentService.tagDetail(request.getUid(), request.getTagId(), request.getTagName(), request.getPage(),request.getVersion());
    }
    @RequestMapping(value = "/acKingdomList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response acKingdomList(AcKingdomListRequest request){
    	return contentService.acKingdomList(request.getUid(), request.getCeTopicId(), request.getResultType(), request.getPage());
    }
}
