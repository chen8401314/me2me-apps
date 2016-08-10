package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/19
 * Time :14:00
 */
@Data
public class LoadAllFeelingDto implements BaseEntity{

    private String tag;

    private long tid;

    private long cid;

    private long uid;

    private String content;

    private int type;

    private String avatar;

    private String nickName;

    private int likeCount;
}
