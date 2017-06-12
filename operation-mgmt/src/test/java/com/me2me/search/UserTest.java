package com.me2me.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.alibaba.fastjson.JSON;
import com.me2me.common.web.Response;
import com.me2me.user.service.UserService;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class UserTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private UserService service;

	@Test
	public void testLiveImgDBUp() {
		Response resp =service.getMBTIResult(318);
		System.out.println(JSON.toJSONString(resp,true));
	}
	
}
