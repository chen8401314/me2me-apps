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
import java.util.List;

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
        List<IosWapx> iosWapx = userMybatisDao.getWapxByIdfaList(event.getIdfa());
        for(IosWapx iosWapxList : iosWapx) {
            if (iosWapx.size() > 0 && iosWapx != null) {
                if(iosWapxList.getChannelTyp() == 0) {
                    long hours = DateUtil.getHoursBetween2Date(new Date(), iosWapxList.getUpdateTime());
                    //控制在一小时
                    if (iosWapxList.getStatus() == 0 && hours <= 1) {
                        //去激活
                        boolean b = fileTransferService.IosWapxActivate(iosWapxList.getCallbackurl());
                        if (b) {
                            iosWapxList.setStatus(1);
                            iosWapxList.setUid(event.getUid());
                            iosWapxList.setUpdateTime(new Date());
                            userMybatisDao.updateWapx(iosWapxList);
                            log.info("update wapx success");
                        } else {
                            log.info("update wapx failure success:false");
                            iosWapxList.setUid(event.getUid());
                            userMybatisDao.updateWapx(iosWapxList);
                        }
                    }
                }else if (iosWapxList.getChannelTyp() == 1){
                    long hours = DateUtil.getHoursBetween2Date(new Date(), iosWapxList.getUpdateTime());
                    //控制在一小时
                    if (iosWapxList.getStatus() == 0 && hours <= 1) {
                        int code = fileTransferService.DaodaoActivate(iosWapxList.getCallbackurl());
                        if(code == 0){
                            iosWapxList.setStatus(1);
                            iosWapxList.setUid(event.getUid());
                            iosWapxList.setUpdateTime(new Date());
                            userMybatisDao.updateWapx(iosWapxList);
                            log.info("update daodao success");
                        }else {
                            log.info("activation daodao failure");
                            iosWapxList.setUid(event.getUid());
                            userMybatisDao.updateWapx(iosWapxList);
                        }
                    }
                }
            }
        }
    }

}
