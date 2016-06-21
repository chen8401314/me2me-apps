package com.me2me.content.widget;

import com.me2me.activity.model.Activity;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.content.dto.LikeDto;
import com.me2me.content.model.Content;
import com.me2me.content.model.ContentLikesDetails;
import com.me2me.content.service.ContentService;
import com.me2me.monitor.event.MonitorEvent;
import com.me2me.monitor.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/21
 * Time :16:43
 */
@Component
@Slf4j
public class ActivityLikes extends AbstractLikes implements Likes{

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private ActivityService activityService;

    @Override
    public Response likes(LikeDto likeDto) {
     return null;
    }
}
