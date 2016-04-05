package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Data
public class ContentDto implements BaseEntity {

    private String feeling;

    private String content;

    private String imageUrls;

    private int contentType;

    private long forwardCid;

    private int type;

    private long uid;

    private long cid;

    private Date createTime;
}
