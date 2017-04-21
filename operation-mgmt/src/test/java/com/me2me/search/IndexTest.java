package com.me2me.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.alibaba.fastjson.JSON;
import com.me2me.common.web.Response;
import com.me2me.search.service.SearchService;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class IndexTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private SearchService searchService;
	//@Test
	public void indexKingdomData() {
		try {
			searchService.indexKingdomData(true);
			Thread.sleep(3 * 1000);
			ThreadPool.shutdown();
			while (!ThreadPool.isTerminated()) {
				Thread.sleep(3 * 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 @Test
	public void indexUserData() {
		try {
			searchService.indexUserData(true);
			Thread.sleep(3 * 1000);
			ThreadPool.shutdown();
			while (!ThreadPool.isTerminated()) {
				Thread.sleep(3 * 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
