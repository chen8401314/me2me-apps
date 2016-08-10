package com.me2me.sms.service;

import com.me2me.sms.dto.PushLogDto;
import com.me2me.sms.dto.PushMessageAndroidDto;
import com.me2me.sms.dto.PushMessageIosDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/30
 * Time :16:29
 */
public interface XgPushService {

    /**
     * 单个设备推送
     * @return
     */
    PushLogDto pushSingleDevice(PushMessageAndroidDto pushMessageAndroidDto);

    /**
     * 所有设备推送
     * @return
     */
    PushLogDto pushAllDevice(PushMessageAndroidDto pushMessageAndroidDto);

    /**
     * ios单个设备推送
     * @return
     */
    PushLogDto pushSingleDeviceIOS(PushMessageIosDto pushMessageIosDto);

    /**
     * ios所有设备推送
     * @return
     */
    PushLogDto pushAllDeviceIOS(PushMessageIosDto pushMessageIosDto);



}
