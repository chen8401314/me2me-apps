package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.io.service.FileTransferService;
import com.me2me.web.request.GetQiniuAccessTokenRequest;
import com.me2me.web.request.WeChatRequest;
import com.me2me.web.request.WxJsApiTicketRequest;

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
@RequestMapping(value = "/api/io")
public class IO extends BaseController {

    @Autowired
    private FileTransferService fileTransferService;

    /**
     * 获取七牛Token
     */
    @ResponseBody
    @RequestMapping(value = "/getQiniuAccessToken",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getQiniuAccessToken(GetQiniuAccessTokenRequest request){
        return fileTransferService.getQiniuAccessToken(request.getBucket());
    }


    /**
     * h5第三方登录 调用第三方接口(出外网)
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserInfo(WeChatRequest request) throws Exception {
        return fileTransferService.getUserInfo(request.getCode());
    }
    /**
     * h5 jsapi
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getWxJsApiTicket",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String getWxJsApiTicket(WxJsApiTicketRequest request) throws Exception {
        return fileTransferService.getWxJsApiTicket(request.getAppId(),request.getAppSecert());
    }
}
