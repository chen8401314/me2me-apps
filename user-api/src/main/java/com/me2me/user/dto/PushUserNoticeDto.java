package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/29.
 */
@Data
public class PushUserNoticeDto implements BaseEntity {

    private long uid;

    private int counter;
}
