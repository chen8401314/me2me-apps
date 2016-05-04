package com.me2me.web;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.content.dto.EditorContentDto;
import com.me2me.content.service.ContentService;
import com.me2me.user.dto.UserSignUpDto;
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
     * 活动列表
     * @return
     */
    @RequestMapping(value = "/activity",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response activity(ActivityRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
       return activityService.getActivity(request.getSinceId());
    }



    /**
     * 专属（老徐那边的数据接口）
     * @return
     */
    @RequestMapping(value = "/special",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response special(ShowContentsRequest request){
        return null;
    }

    /**
     * 用户日记，直播，活动
     * @param request
     * @return
     */
    @RequestMapping(value = "/newest",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response newest(NewestRequest request){
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        return contentService.getNewest(request.getSinceId(),request.getUid());
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
        return contentService.getAttention(request.getSinceId(),request.getUid());
    }



}
