package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by pc329 on 2017/3/17.
 */
@Data
public class BangDanDto implements BaseEntity {

    private List<BangDanData> listData = Lists.newArrayList();

    @Data
    public static class BangDanData implements BaseEntity{

        private long sinceId;

        private long listId;

        private String title;

        private int type;

        private String summary;

        private String bgColor;

        private String coverImage;

        private int coverWidth;

        private int coverHeight;

        private int isShowName;

        private int subType;

        private List<BangDanInnerData> subList = Lists.newArrayList();

        @Data
        private class BangDanInnerData {
            private long subListId;

            private int subType;

            private long uid;

            private String avatar;

            private String nickName;

            private int v_lv;

            private int isFollowed;

            private int isFollowMe;

            private int contentType;

            private int favorite;

            private long id;

            private long cid;

            private long topicId;

            private long forwardCid;

            private String title;

            private String coverImage;

            private int internalStatus;

            private int favoriteCount;

            private int readCount;

            private int likeCount;

            private int reviewCount;

            private String introduced;

        }
    }


}
