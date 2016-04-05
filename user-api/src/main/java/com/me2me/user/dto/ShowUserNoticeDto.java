package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/1.
 */
@Data
public class ShowUserNoticeDto implements BaseEntity {

    private long id;

    private int noticeType;

    private long fromUid;

    private long toUid;

    private String fromNickName;

    private String toNickName;

    private String tag;

    private String coverImage;

    private String summary;

    private int likeCount;

    private String fromAvatar;

    private int readStatus;

    private Date createTime;



}
