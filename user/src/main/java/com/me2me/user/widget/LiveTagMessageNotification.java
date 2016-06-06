package com.me2me.user.widget;

import com.me2me.common.web.Specification;
import com.me2me.sms.dto.PushLogDto;
import com.me2me.sms.dto.PushMessageAndroidDto;
import com.me2me.sms.dto.PushMessageDto;
import com.me2me.sms.dto.PushMessageIosDto;
import com.me2me.sms.service.XgPushService;
import com.me2me.user.model.UserDevice;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/6.
 */
@Component
public class LiveTagMessageNotification implements MessageNotification {

    @Autowired
    private XgPushService xgPushService;

    @Autowired
    private UserService userService;

    @Override
    public void notice(String title, long targetUid, long sourceUid) {
        UserDevice device = userService.getUserDevice(targetUid);
        if(device != null) {
            PushMessageDto pushMessageDto = new PushMessageDto();
            pushMessageDto.setToken(device.getDeviceNo());
            pushMessageDto.setDevicePlatform(device.getPlatform());
            pushMessageDto.setContent("你的直播:" + title + "收到了1个新感受");
            //
            if (device.getPlatform() == 1) {
                PushMessageAndroidDto pushMessageAndroidDto = new PushMessageAndroidDto();
                pushMessageAndroidDto.setTitle(pushMessageDto.getContent());
                pushMessageAndroidDto.setToken(device.getDeviceNo());
                pushMessageAndroidDto.setMessageType(Specification.PushMessageType.LIVE_TAG.index);
                pushMessageAndroidDto.setContent(pushMessageDto.getContent());
                  PushLogDto pushLogDto = xgPushService.pushSingleDevice(pushMessageAndroidDto);
                if (pushLogDto != null) {
                    pushLogDto.setMeaageType(Specification.PushMessageType.LIVE_TAG.index);
                   userService.createPushLog(pushLogDto);
                }
            } else {
                PushMessageIosDto pushMessageIosDto = new PushMessageIosDto();
                pushMessageIosDto.setTitle(pushMessageDto.getContent());
                pushMessageIosDto.setToken(device.getDeviceNo());
                pushMessageIosDto.setContent(pushMessageDto.getContent());
                PushLogDto pushLogDto = xgPushService.pushSingleDeviceIOS(pushMessageIosDto);
                if (pushLogDto != null) {
                    pushLogDto.setMeaageType(Specification.PushMessageType.LIVE_TAG.index);
                    userService.createPushLog(pushLogDto);
                }
            }
        }
    }


}
