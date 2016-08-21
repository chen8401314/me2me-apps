package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/13
 * Time :19:22
 */
@Data
public class LiveCoverDto implements BaseEntity{

    private String title;

    private Date createTime;

    private long lastUpdateTime;

    private String coverImage;

    private long uid ;

    private String avatar;

    private String nickName;

    private int topicCount;

    private int reviewCount;

    // 阅读数（暂时未添加）
    private int readCount;

    // 成员数（暂时未添加）
    private int membersCount;

    private int internalStatus;
}
