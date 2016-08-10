package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/23
 * Time :21:14
 */
@Data
public class ResultKingTopicDto  implements BaseEntity {

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
