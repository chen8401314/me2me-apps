package com.me2me.user.widget;

import com.me2me.sms.dto.PushLogDto;
import com.me2me.sms.dto.PushMessageAndroidDto;
import com.me2me.sms.dto.PushMessageDto;
import com.me2me.sms.dto.PushMessageIosDto;
import com.me2me.sms.service.XgPushService;
import com.me2me.user.model.UserDevice;
import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/6
 * Time :17:50
 */
public class AbstractMessageNotification {

    @Autowired
    protected XgPushService xgPushService;

    @Autowired
    protected UserService userService;

    public void notice(String content, long targetUid, long sourceUid,int type){
        if(targetUid == sourceUid ){
            return;
        }
        UserDevice device = userService.getUserDevice(targetUid);
        if(device != null) {
            PushMessageDto pushMessageDto = new PushMessageDto();
            pushMessageDto.setToken(device.getDeviceNo());
            pushMessageDto.setDevicePlatform(device.getPlatform());
            pushMessageDto.setContent(content);
            pushMessageDto.setType(type);
            if (device.getPlatform() == 1) {
                PushMessageAndroidDto pushMessageAndroidDto = new PushMessageAndroidDto();
                pushMessageAndroidDto.setTitle(pushMessageDto.getContent());
                pushMessageAndroidDto.setToken(device.getDeviceNo());
                pushMessageAndroidDto.setMessageType(type);
                pushMessageAndroidDto.setContent(pushMessageDto.getContent());
                PushLogDto pushLogDto = xgPushService.pushSingleDevice(pushMessageAndroidDto);
                if (pushLogDto != null) {
                    pushLogDto.setMessageType(type);
                    userService.createPushLog(pushLogDto);
                }
            } else {
                PushMessageIosDto pushMessageIosDto = new PushMessageIosDto();
                pushMessageIosDto.setTitle(pushMessageDto.getContent());
                pushMessageIosDto.setToken(device.getDeviceNo());
                pushMessageIosDto.setContent(pushMessageDto.getContent());
                pushMessageIosDto.setMessageType(type);
                PushLogDto pushLogDto = xgPushService.pushSingleDeviceIOS(pushMessageIosDto);
                if (pushLogDto != null) {
                    pushLogDto.setMessageType(type);
                    userService.createPushLog(pushLogDto);
                }
            }
        }

    }
}
