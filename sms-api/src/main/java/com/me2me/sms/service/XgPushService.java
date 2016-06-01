package com.me2me.sms.service;


import com.me2me.sms.dto.PushMessageDto;
import com.me2me.sms.dto.PushMessageDtoIos;
import org.json.JSONObject;

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
    JSONObject pushSingleDevice(PushMessageDto pushMessageDto);

    /**
     * 所有设备推送
     * @return
     */
    JSONObject pushAllDevice(PushMessageDto pushMessageDto);

    /**
     * ios单个设备推送
     * @return
     */
    JSONObject pushSingleDeviceIOS(PushMessageDtoIos pushMessageDtoIos);

    /**
     * ios所有设备推送
     * @return
     */
    JSONObject pushAllDeviceIOS(PushMessageDtoIos pushMessageDtoIos);

    /**
     * 查询推送状态
     */
    JSONObject queryPushStatus();

    /**
     * 查询设备数量
     * @return
     */
    JSONObject queryDeviceCount();
}
