package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.dto.WriteTagDto;
import com.me2me.content.service.ContentService;
import com.me2me.user.dto.*;
import com.me2me.user.service.UserService;
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
public class Contents {

    @Autowired
    private ContentService contentService;

    /**
     * 精选接口
     * @return
     */
    @RequestMapping(value = "/highQuality",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response highQuality(SquareRequest request){
        if(request.getSinceId()==-1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.highQuality(request.getSinceId());
    }

    /**
     * 广场接口
     * @return
     */
    @RequestMapping(value = "/square",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response square(SquareRequest request){
        if(request.getSinceId()==-1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.square(request.getSinceId());
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
        return contentService.publish(contentDto);
    }

    /**
     * 用户点赞接口
     * @return
     */
    @RequestMapping(value = "/likes",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response publish(LikeRequest request){
        LikeDto likeDto = new LikeDto();
        likeDto.setUid(request.getUid());
        likeDto.setCid(request.getCid());
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





}
