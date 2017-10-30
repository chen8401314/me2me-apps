package com.me2me.sms.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * 小米Push
 * @author pc340
 *
 */
@Slf4j
@Component
public class XiaomiPushServiceImpl implements SpecialPushService {

	@Override
	public void payloadByIdExtra(String uid, String message,
			Map<String, String> extraMaps) {
		
	}

}
