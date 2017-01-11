package com.me2me.sms.service;

import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/8/9.
 */
public interface JPushService {

    static final String masterSecret = "467e198daf63ff0596b0784d";

    static final String appKey = "9222161c4591256016b4efee";


    /**
     * 系统公告通知来用
     */
    void payloadAll(String message);

    void payloadById(String regId,String message);

    void payloadByIdExtra(String uid,String message,Map<String,String> extraMaps);
    
    void payloadByIdForMessage(String regId,String message);

    void payloadByIdsExtra(boolean isAll, String[] uids, String message, Map<String,String> extraMaps);
}
