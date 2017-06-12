package com.me2me.user.dto;

import java.util.Date;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-05-24
 */
@Data
public class LastEmotionInfoDto implements BaseEntity {

    
	 private long id;

     private String emotionName;

     
     private int happyValue;
     
     private int freeValue;
     
     private long topicId;
     
     private int isSummary;
     
     private int recordCount;
     
     private long timeInterval;
     
     private Date createTime;
     
     private String dateStr;
     
     private EmotionPack emotionPack;
        
    
    public static EmotionPack createEmotionPack(){
        return new EmotionPack();
    }

    @Data
    public static class EmotionPack implements BaseEntity {

        private long id;

        private String title;
        
        private String content;

        private String image;
        
        private String thumb;
        
        private long w;
        
        private long h;
        
        private long thumb_w;
        
        private long thumb_h;
        
        private String extra;
        
        private int emojiType;
        
    }
}
