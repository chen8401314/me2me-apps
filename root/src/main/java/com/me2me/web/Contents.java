package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.ReviewDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.service.ContentService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


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
        contentDto.setForwardCid(request.getForwardCid());
        contentDto.setImageUrls(request.getImageUrls());
        contentDto.setType(request.getType());
        contentDto.setTitle(request.getTitle());
        contentDto.setRights(request.getRights());
        contentDto.setCoverImage(request.getCoverImage());
        contentDto.setForwardTitle(request.getForwardTitle());
        if(contentDto.getType()!=2) {
            // 用户UGC入口
           // return contentService.publish(contentDto);
            return contentService.publish2(contentDto);
        }else{
            // 小编发布入口
            return contentService.editorPublish(contentDto);
        }
    }

    /**
     * 用户点赞接口
     * @return
     */
    @RequestMapping(value = "/likes",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response likes(LikeRequest request){
        LikeDto likeDto = new LikeDto();
        likeDto.setUid(request.getUid());
        likeDto.setCid(request.getCid());
        likeDto.setAction(request.getAction());
        likeDto.setType(request.getType());
        return contentService.like(likeDto);
    }

    /**
     *  用户贴标签
     * @return
     */
    @RequestMapping(value = "/writeTag",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response writeTag(WriteTagRequest request){
        WriteTagDto writeTagDto = new WriteTagDto();
        writeTagDto.setCid(request.getCid());
        writeTagDto.setTag(request.getTag());
        writeTagDto.setUid(request.getUid());
        writeTagDto.setCustomerId(request.getCustomerId());
        return contentService.writeTag(writeTagDto);
    }

    /**
     *  用户删除发布内容
     * @return
     */
    @RequestMapping(value = "/deleteContent",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response deleteContent(DeleteContentRequest request){
        return contentService.deleteContent(request.getId());
    }

    /**
     * 用户发布列表接口
     * @return
     */
    @RequestMapping(value = "/myPublish",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response myPublish(MyPublishContentRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.myPublish(request.getCustomerId(),request.getSinceId());
    }

    /**
     * 内容详情接口
     * @return
     */
    @RequestMapping(value = "/getContentDetail",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getContentDetail(ContentDetailRequest request){

        return contentService.getContentDetail(request.getId(),request.getUid());
    }

    /**
     * 用户资料卡
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getSelectedData(UserInfoRequest request){
        return  contentService.getUserData(request.getCustomerId(),request.getUid());
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
        return contentService.getSelectedData(request.getSinceId(),request.getUid());
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
     * 活动列表接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/activities",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response activities(ActivitiesRequest request){

        return contentService.getActivities(request.getSinceId(),request.getUid());
    }

    /**
     * 文章评论接口
     * @return
     */
    @RequestMapping(value = "/review",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response review(ReviewRequest request){
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setUid(request.getUid());
        reviewDto.setCid(request.getCid());
        reviewDto.setReview(request.getReview());
        reviewDto.setType(request.getType() == 0 ? 1: request.getType());
        return contentService.createReview(reviewDto);
    }

    /**
     * 文章评论列表
     * @return
     */
    @RequestMapping(value = "/reviewList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response review(ReviewListRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.reviewList(request.getCid(),request.getSinceId());
    }

    @RequestMapping(value = "/getArticleComments",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getArticleComments(ArticleCommentsRequest request){
        return contentService.getArticleComments(request.getId());
    }

}
