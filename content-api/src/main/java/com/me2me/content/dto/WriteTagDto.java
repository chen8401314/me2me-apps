package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/24
 * Time :15:26
 */
@Data
public class WriteTagDto implements BaseEntity{

    private String tag;

    private long uid;

    private long cid;

    private long tagId;

    private long customerId;

    private int type;
}
