package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/23
 * Time :20:08
 */
@Data
public class ShowKingTopicDto implements BaseEntity {

    private List<KingTopicElement> result = Lists.newArrayList();

    public KingTopicElement createKingTopicElement(){
        return new KingTopicElement();
    }

    @Data
    public static class KingTopicElement implements BaseEntity {

        private String avatar;

        private String coverImage;

        private String nickName;

        private String title;

        private long uid;

        private long topicId;

        private int reviewCount;

        private int likeCount;

        private Date createTime;
    }

}
