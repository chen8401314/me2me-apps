package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/21
 * Time :10:26
 */
@Data
public class ShowLiveDto implements BaseEntity {

    private String avatar;

    private String coverImage;

    private Date createTime;

    private String nickName;

    private int status;

    private String title;

    private int favorite;

    private long uid;

    private long topicId;

    private int favoriteCount;

    private int personCount;

    private int reviewCount;

    private int isLike;

    private int likeCount;

    private long cid;

    private long updateTime;

    private int isFollowed;

    private int v_lv;
}
