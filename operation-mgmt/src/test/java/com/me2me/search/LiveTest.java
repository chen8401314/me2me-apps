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
import com.me2me.live.dto.SpeakDto;
import com.me2me.live.service.LiveService;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class LiveTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private LiveService liveService;

	//@Test
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
	@Test
	public void testMyTopic(){
		Response response = liveService.getMyTopic(204481, System.currentTimeMillis(), null);
		System.out.println(response);
	}
	//@Test
	public void testStealCoins() {
		Response resp =liveService.stealKingdomCoin(318, 2852);
		System.out.println(JSON.toJSONString(resp));
	}
	//@Test
	public void testSpeak(){
		SpeakDto dto = new SpeakDto();
		dto.setType(0);
		dto.setContentType(0);
		dto.setFragment("我们现在有两个输入框，但是当您在其中一个输入温度时，另一个不更新。 这违反了我们的要求：我们希望保持它们同步。我们也不能从Calculator显示BoilingVerdict。 计算器不知道当前温度，因为它隐藏在TemperatureInput中。提升state首先，我们将写两个函数来将摄氏度转换为华氏度，然后返回：");
		dto.setUid(318);
		dto.setTopicId(48);
		
		Response resp =liveService.speak(dto);
		System.out.println(JSON.toJSONString(resp));
	}
}
