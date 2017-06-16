package com.me2me.pay.service;

import com.me2me.pay.channel.PayStrategy;
import com.me2me.common.web.Response;

/**
 * Created by pc329 on 2017/5/15.
 */
public interface PayService {



    // 充值业务
    Response recharge(PayStrategy payStrategy);

    // 消费业务
    Response consume();

    // 提现业务
    void cash();



}
