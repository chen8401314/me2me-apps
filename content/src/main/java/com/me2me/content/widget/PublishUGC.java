package com.me2me.content.widget;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.ContentDto;
import com.me2me.content.dto.CreateContentSuccessDto;
import com.me2me.content.event.PublishUGCEvent;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.monitor.service.MonitorService;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.user.dto.ModifyUserCoinDto;
import com.me2me.user.rule.Rules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
@Slf4j
public class PublishUGC extends AbstractPublish implements Publish {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private ApplicationEventBus applicationEventBus;



    @Override
    public Response publish(ContentDto contentDto) {
        log.info("PublishUGC publish");
        activityService.joinActivity(contentDto.getContent(),contentDto.getUid());
        log.info("join Activity");
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.CONTENT_PUBLISH.index,0,contentDto.getUid()));
        log.info("monitor PublishUGC ");
        contentDto.setTitle("");
        Response response =  super.publish(contentDto);
        if(response.getData() instanceof CreateContentSuccessDto){
            CreateContentSuccessDto contentSuccessDto = (CreateContentSuccessDto) response.getData();
            long cid = contentSuccessDto.getId();
            applicationEventBus.post(new PublishUGCEvent(cid));
        }

        return response;
    }

}
