package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Author: 马秀成
 * Date: 2017/1/7
 */
@Data
public class ActualAndHistoryDto implements BaseEntity {

    private List<ActualAndHistoryElement> actualAndHistoryList = Lists.newArrayList();

    private List<ActualAndHistoryElement> myActualAndHistoryList = Lists.newArrayList();

    public ActualAndHistoryElement createActualAndHistoryElement() {
        return new ActualAndHistoryElement();
    }

    @Data
    public class ActualAndHistoryElement implements BaseEntity {

        private String liveImage;

        private String title;

        private long topicId;

        private long hot;

        private long uid;

        private int ranks;

        private String nickName;

        private String avatar;

    }

}
