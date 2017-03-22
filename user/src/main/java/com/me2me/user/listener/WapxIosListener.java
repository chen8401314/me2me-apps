package com.me2me.user.listener;

import com.google.common.eventbus.Subscribe;
import com.me2me.common.utils.DateUtil;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.io.service.FileTransferService;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.WapxIosEvent;
import com.me2me.user.model.IosWapx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

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
            long hours = DateUtil.getHoursBetween2Date(new Date() ,iosWapx.getUpdateTime());
            //控制在一小时
            if(iosWapx.getStatus() == 0 && hours <= 1){
                //去激活
                boolean b = fileTransferService.IosWapxActivate(iosWapx.getCallbackurl());
                if(b){
                    iosWapx.setStatus(1);
                    iosWapx.setUid(event.getUid());
                    userMybatisDao.updateWapx(iosWapx);
                    log.info("update wapx success");
                }else {
                    log.info("update wapx failure success:false");
                }
            }
        }
    }

}
