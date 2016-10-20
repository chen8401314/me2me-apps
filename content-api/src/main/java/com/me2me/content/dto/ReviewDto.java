package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/3
 * Time :17:01
 */
@Data
public class ReviewDto implements BaseEntity{

    private long id;

    private long uid;

    private long cid;

    private String review;

    private int type;

    private int isAt;

    private long atUid;

    private String extra;
}
