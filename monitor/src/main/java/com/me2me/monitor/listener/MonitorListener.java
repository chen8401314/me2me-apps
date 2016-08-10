package com.me2me.monitor.listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.monitor.service.MonitorService;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.monitor.model.AccessTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/23.
 */
@Component
public class MonitorListener {

    private final ApplicationEventBus applicationEventBus;

    private final MonitorService monitorService;


    @Autowired
    public MonitorListener(ApplicationEventBus applicationEventBus,MonitorService monitorService){
        this.applicationEventBus = applicationEventBus;
        this.monitorService = monitorService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void monitor(MonitorEvent monitorEvent){
        AccessTrack at = new AccessTrack();
        at.setCreateTime(new Date());
        at.setActionType(monitorEvent.getActionType());
        at.setChannel(Long.valueOf(monitorEvent.getChannel()));
        at.setType(monitorEvent.getType());
        at.setUid(monitorEvent.getUid());
        monitorService.mark(at);
    }


}
