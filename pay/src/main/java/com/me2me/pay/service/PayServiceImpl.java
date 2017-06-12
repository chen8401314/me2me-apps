package com.me2me.pay.service;

import com.me2me.pay.channel.PayStrategy;
import com.me2me.common.web.Response;
import org.springframework.stereotype.Service;

/**
 * Created by pc329 on 2017/5/15.
 */
@Service
public class PayServiceImpl implements PayService {


    @Override
    public Response recharge(PayStrategy payStrategy) {
        payStrategy.pay();
        return Response.success();

    }

    @Override
    public Response consume() {
        return Response.success();
    }

    @Override
    public void cash() {

    }
}
