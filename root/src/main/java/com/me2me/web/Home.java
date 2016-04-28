package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.content.dto.EditorContentDto;
import com.me2me.content.service.ContentService;
import com.me2me.user.dto.UserSignUpDto;
import com.me2me.user.service.UserService;
import com.me2me.web.request.BindAccountRequest;
import com.me2me.web.request.ShowContentsRequest;
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
    private UserService userService;

    @Autowired
    private ContentService contentService;

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/hottest",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response hottest(BindAccountRequest bindAccountRequest){
        return null;
    }

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/special",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response special(ShowContentsRequest showContentsRequest){
        return null;
    }

    @RequestMapping(value = "/newest",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response newest(ShowContentsRequest showContentsRequest){
        return null;
    }

    @RequestMapping(value = "/attention ",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response attention (ShowContentsRequest showContentsRequest){
        return null;
    }



}
