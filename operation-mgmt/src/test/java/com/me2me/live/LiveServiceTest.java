package com.me2me.live;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.me2me.cache.service.CacheService;
import com.me2me.user.model.User;

import lombok.Data;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class LiveServiceTest {
	
	@Autowired
	CacheService cache;
	/**
	 * redis存储对象测试
	 * @author zhangjiwei
	 * @date Aug 17, 2017
	 */
	@Test
	public void testCacheJavaObject() {
		User user = new User();
		user.setUserName("test哈哈");
		Map<String, Object> map = new HashMap<>();
		map.put("a", "1");
		map.put("b", 4456);
		map.put("c", user);
		List<Map<String, Object>> dataList = new ArrayList<>();
		dataList.add(map);
		String key1="___test__java_obj",key2="___test__java_map",key3="___test__java_list";
		
		cache.cacheJavaObject(key1, user, 10);
		cache.cacheJavaObject(key2, map, 10);
		cache.cacheJavaObject(key3, dataList, 10);
		
		User getUser= (User) cache.getJavaObject(key1);
		Assert.assertEquals(getUser.getUserName(), "test哈哈");
		
		Map<String, Object> getmap =(Map<String, Object>) cache.getJavaObject(key2);
		Assert.assertEquals(getmap.get("a"), "1");
		Assert.assertEquals(getmap.get("b"), 4456);
		Assert.assertEquals(((User)getmap.get("c")).getUserName(), "test哈哈");
		
		List<Map<String, Object>> getdataList=(List<Map<String, Object>>) cache.getJavaObject(key3);
		Assert.assertEquals((getdataList.get(0).get("a")), "1");
	}
	
	@Test
	public void testCacheNoSerializedObject(){
		
		TestObject obj = new TestObject();
		obj.setName("不错哦");
		cache.cacheJavaObject("___no_serialized_obj__", obj,10);
		
		TestObject newObj = (TestObject) cache.getJavaObject("___no_serialized_obj__");
		Assert.assertEquals(obj.getName(), newObj.getName());
		
	}
}
