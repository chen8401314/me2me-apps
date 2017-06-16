package com.me2me.live.dto;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-6-15
 */
@Data
public class GetKingdomPriceDto implements BaseEntity {


    private String title;
    
    private int topicPrice;
    
    private double topicRMB;
    
    private int topicPriceChanged;
    
    private int distanceListed;
    
    private int beatTopicPercentage;
    
    private int updateTextCount;
    
    private int updateImageCount;
    
    private int updateVoiceCount;
    
    private int updateVideoCount;
    
    private int voteCount;
    
    private int teaseCount;
    
    private int updateDaysCount;
    
    private int replyTextCount;
    
    private int topicUserCount;
    
    private int readCount;
    
    private int shareCount;
    
    private int outerReadCount;

    private double careDegree;
    
    private double approvalDegree;
    
    private int isSell;
    
    private long sellUid;
    
    private  List<TopicPriceChangeElement> topicPriceChangedList = Lists.newArrayList();
    
    
    public static TopicPriceChangeElement createTopicPriceChangeElement(){
        return new TopicPriceChangeElement();
    }

    @Data
    public static class TopicPriceChangeElement implements BaseEntity {

        private int topicPrice;


    }
}
