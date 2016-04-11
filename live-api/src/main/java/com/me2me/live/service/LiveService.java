package com.me2me.live.service;

import com.me2me.common.web.Response;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11.
 */
public interface LiveService {


    /**
     * 创建直播
     * @return
     */
    Response createLive();


    /**
     * 获取直播时间线(轮询机制)
     * @return
     */
    Response getLiveTimeline();
}
