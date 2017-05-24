package com.me2me.web;

import com.me2me.activity.channel.AliPayStrategy;
import com.me2me.activity.channel.WxPayStrategy;
import com.me2me.activity.service.PayService;
import com.me2me.common.web.Response;

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
@RequestMapping(value = "/api/pay")
public class Payment extends BaseController {


    @Autowired
    private PayService payService;

    @Autowired
    private AliPayStrategy aliPayStrategy;

    @Autowired
    private WxPayStrategy wxPayStrategy;

    /**
     * 充值业务
     */
    @ResponseBody
    @RequestMapping(value = "/recharge",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response recharge(int type){
        if(type==0){
            return payService.recharge(aliPayStrategy);
        }else{
            return payService.recharge(wxPayStrategy);
        }

    }



}
