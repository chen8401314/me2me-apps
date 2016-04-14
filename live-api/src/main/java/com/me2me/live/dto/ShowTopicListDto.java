package com.me2me.live.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/13
 * Time :17:16
 */
@Data
public class ShowTopicListDto implements BaseEntity{

    private List<ShowTopicElement> showTopicElements = Lists.newArrayList();

    public static ShowTopicElement createShowTopicElement(){
        return new ShowTopicElement();
    }

    @Data
    public static  class ShowTopicElement implements BaseEntity{

        private long topicId;

        private String title;

        private String coverImage;

        private long uid;

        private String avatar;

        private Date createTime;

        private int LastContentType;

        private String lastFragment;

        private String lastFragmentImage;

        private String nickName;
    }
}
