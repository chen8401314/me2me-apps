package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by 马秀成 on 2016/12/5.
 */
@Data
public class QiActivityDto implements BaseEntity {

	private int isBind;//0没绑过，1绑定过
    private String mobile;
    private long auid;
    private int userStatus;

    private List<TopicElement> topicList = Lists.newArrayList();

    public TopicElement createElement(){
        return new TopicElement();
    }

    @Data
    public class TopicElement implements BaseEntity{

        private int stage;

        private String title;

        private String liveImage;

        private Long topicId;
        
        private long hot;
    }
}
