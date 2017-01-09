package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Author: 马秀成
 * Date: 2017/1/6
 */
@Data
public class NewYearDto implements BaseEntity {

    private List<NewYearElement> newYearList = Lists.newArrayList();

    public NewYearElement createNewYearElement(){
        return new NewYearElement();
    }

    @Data
    public class NewYearElement implements BaseEntity {

        private String liveImage;

        private String title;

        private long topicId;

        private int isFollowMe;

        private int isFollowed;

        private long hot;

        private String avatar;

        private long uid;

        private String nickName;

    }

}
