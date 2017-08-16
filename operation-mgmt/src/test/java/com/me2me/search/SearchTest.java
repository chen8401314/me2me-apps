package com.me2me.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.me2me.common.web.Response;
import com.me2me.search.service.SearchService;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class SearchTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private SearchService searchService;
	//@Test
	public void testRecommendUser() {
		int uid = 298;
		try {
			Response resp = searchService.recommendUser(uid, 1, 10);
			System.out.println(JSON.toJSONString(resp, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//@Test
	public void testRecommendKingdom() {
		int uid = 298;
		try {
			Response resp = searchService.recommendKingdom(uid, 1, 10);
			System.out.println(JSON.toJSONString(resp, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testRecommendTag() {
		int uid = 298;
		try {
			Response resp = searchService.recommendTags("莎士比亚",1);
			System.out.println(JSON.toJSONString(resp, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
