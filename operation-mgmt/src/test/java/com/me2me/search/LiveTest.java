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
	public void testLiveImgDBUp() {
	
		int topicId = 2762;
		int fId = 847813;//847813;
		try {
			System.out.println("-----up----------------");
			Response resp = liveService.kingdomImgDB(topicId, 1, fId,1);
			System.out.println(JSON.toJSONString(resp, true));
			
			System.out.println("-----down----------------");
			Response resp2 = liveService.kingdomImgDB(topicId, 0, fId,1);
			System.out.println(JSON.toJSONString(resp2, true));
			
			
			System.out.println("-----click----------------");
			
			Response resp3 = liveService.kingdomImgDB(topicId, 2, fId,1);
			System.out.println(JSON.toJSONString(resp3, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
