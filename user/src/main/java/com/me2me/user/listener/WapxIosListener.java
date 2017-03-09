package com.me2me.user.listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.io.service.FileTransferService;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.WapxIosEvent;
import com.me2me.user.model.IosWapx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Author: 马秀成
 * Date: 2017/3/9
 */
@Component
@Slf4j
public class WapxIosListener {

    private final ApplicationEventBus applicationEventBus;

    private final UserMybatisDao userMybatisDao;

    private final FileTransferService fileTransferService;

    @Autowired
    public WapxIosListener(ApplicationEventBus applicationEventBus, UserMybatisDao userMybatisDao, FileTransferService fileTransferService) {
        this.applicationEventBus = applicationEventBus;
        this.userMybatisDao = userMybatisDao;
        this.fileTransferService = fileTransferService;
    }

    @PostConstruct
    public void init(){
        this.applicationEventBus.register(this);
    }

    @Subscribe
    public void wapxIOS(WapxIosEvent event){
        IosWapx iosWapx = userMybatisDao.getWapxByIdfa(event.getIdfa());
        if(iosWapx != null){
            if(iosWapx.getStatus() == 0){
                //去激活
                boolean b = fileTransferService.IosWapxActivate(iosWapx.getUdid() ,iosWapx.getApp() ,iosWapx.getIdfa() ,iosWapx.getOpenudid());
                if(b){
                    iosWapx.setStatus(1);
                    userMybatisDao.updateWapx(iosWapx);
                    log.info("update wapx success");
                }
            }
        }
    }

}
