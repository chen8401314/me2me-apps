package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.io.service.FileTransferService;
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
@RequestMapping(value = "/api/io")
public class IO {

    @Autowired
    private FileTransferService fileTransferService;
    /**
     * 收藏夹
     */
    @ResponseBody
    @RequestMapping(value = "/getQiniuAccessToken",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getQiniuAccessToken(){
        return fileTransferService.getQiniuAccessToken();
    }



}
