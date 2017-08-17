package com.me2me.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.me2me.mgmt.task.index.UserLogAnalyzeTask;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class UserLogTaskTest {
	@Autowired
	UserLogAnalyzeTask task;
	
	@Test
	public void testAnalyze(){
		task.userIndexJob();
	}
}
