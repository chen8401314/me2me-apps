package com.me2me.user.dto;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.me2me.user.dto.PhotoDto.Photo;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-05-24
 */
@Data
public class SummaryEmotionInfoDto implements BaseEntity {

      private String dateStr;
	
	  private List<EmotionData> emotionData = Lists.newArrayList();

	    public static  EmotionData create(){
	        return new EmotionData();
	    }

	    @Data
	    public static class EmotionData implements BaseEntity {

	        private int percentage;
	        
	        private long happyValue;
	        
	        private long freeValue;

	        private String emotionName;

	    }
}
