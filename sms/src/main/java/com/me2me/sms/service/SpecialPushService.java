package com.me2me.sms.service;

import java.util.Map;

/**
 * 除极光推送外的特殊Push
 * @author pc340
 *
 */
public interface SpecialPushService {

	void payloadByIdExtra(String uid,String message,Map<String,String> extraMaps);
}
