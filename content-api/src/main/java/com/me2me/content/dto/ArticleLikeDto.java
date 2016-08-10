package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/15
 * Time :13:58
 */
@Data
public class ArticleLikeDto implements BaseEntity{

    private long cid;

    private long uid;

    private int action;

}
