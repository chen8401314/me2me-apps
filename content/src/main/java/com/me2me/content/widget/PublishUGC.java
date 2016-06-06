package com.me2me.content.widget;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.content.dto.ContentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
public class PublishUGC extends AbstractPublish implements Publish {

    @Autowired
    private ActivityService activityService;

    @Override
    public Response publish(ContentDto contentDto) {
        activityService.joinActivity(contentDto.getContent(),contentDto.getUid());
        return super.publish(contentDto);
    }

}
