package com.me2me.mgmt.task.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Specification;
import com.me2me.user.cache.EmotionSummaryModel;
import com.me2me.user.model.EmotionRecord;
import com.me2me.user.service.UserService;

@Component
public class EmotionSummaryPushTask {

	private static final Logger logger = LoggerFactory.getLogger(EmotionSummaryPushTask.class);

	@Autowired
	private CacheService cacheService;
	@Autowired
	private UserService userService;

	@Scheduled(cron = "0 0 12 ? * MON")
	public void emotionSummaryPush() {
		logger.info("情绪周总结提醒任务开始");
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date);
			int n = -1;
			if (cal1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				n = -2;
			}
			String monday;
			cal1.add(Calendar.DATE, n * 7);
			cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			monday = sdf1.format(cal1.getTime());
			Date mondayDate = null;
			mondayDate = sdf1.parse(monday);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date);
			int m = 0;
			if (cal2.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				m = -1;
			}
			String sunday;
			cal2.add(Calendar.DATE, m * 7);
			cal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			sunday = sdf2.format(cal2.getTime());
			Date sundayDate = sdf2.parse(sunday);
			String message = "上周情绪总结";
			JsonObject jsonObject = null;
			List<EmotionRecord> list = userService.getEmotionRecordByStartAndEnd(mondayDate, sundayDate);
			for (EmotionRecord emotionRecord : list) {
				long uid = emotionRecord.getUid();
				EmotionSummaryModel EmotionSummaryModel = new EmotionSummaryModel(sdf.format(mondayDate), uid, "0");
				String isSummaryStr = cacheService.hGet(EmotionSummaryModel.getKey(), EmotionSummaryModel.getField());
				if (StringUtils.isEmpty(isSummaryStr)) {
					jsonObject = new JsonObject();
					jsonObject.addProperty("type", Specification.PushObjectType.EMOTION.index);
					jsonObject.addProperty("messageType", Specification.PushMessageType.EMOTION_SUMMARY.index);
					jsonObject.addProperty("time", date.getTime());// 当前处理的时间
					userService.pushWithExtra(uid + "", message, JPushUtils.packageExtra(jsonObject));
				}
			}
			logger.info("情绪周总结提醒任务结束");
		} catch (Exception ex) {
			logger.error("情绪周总结提醒任务出错", ex);
		}
	}
}
