package com.me2me.pay.channel;

import org.springframework.stereotype.Component;

/**
 * Created by pc329 on 2017/5/15.
 */
@Component
public class AliPayStrategy implements PayStrategy {

    @Override
    public void pay() {
        System.out.println("阿里支付通道");
    }
}
