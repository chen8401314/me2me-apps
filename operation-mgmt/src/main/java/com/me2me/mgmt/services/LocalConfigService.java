package com.me2me.mgmt.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalConfigService {

	@Value("${kingdomH5Url}")
	private String webappUrl;

	
	
	public String getWebappUrl() {
		return webappUrl;
	}
}
