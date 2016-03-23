package com.me2me.core.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Component
public class ApplicationEventBus {

    private final EventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(5));

    public void register(Object object){
        eventBus.register(object);
    }

    public void unRegister(Object object){
        eventBus.unregister(object);
    }

    public void post(Object event){
        eventBus.post(event);
    }

}
