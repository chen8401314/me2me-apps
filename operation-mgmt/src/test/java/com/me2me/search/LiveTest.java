package com.me2me.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.alibaba.fastjson.JSON;
import com.me2me.common.web.Response;
import com.me2me.live.service.LiveService;
import com.me2me.search.service.SearchService;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class LiveTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private LiveService liveService;
	@Test
	public void testLiveImgDB() {
		int topicId = 4;
		int fId = 100;
		try {
			Response resp = liveService.kingdomImgDB(topicId, 0, fId,1);
			System.out.println(JSON.toJSONString(resp, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
