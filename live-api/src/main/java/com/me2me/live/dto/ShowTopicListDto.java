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

        private long cid;

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

        private int status;

        private int reviewCount;

        private int likeCount;

        private int isLike;

        private int personCount;

        private int favorite;

        private int favoriteCount;

        private  List<Tags> tags  =  Lists.newArrayList();

        public static Tags createTags(){
            return new Tags();
        }

        @Data
        public static  class Tags implements BaseEntity{

            private String tag;

            private long tid;

            private int likeCount;

            private long cid;
        }

    }
}
