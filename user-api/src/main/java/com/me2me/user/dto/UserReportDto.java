package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/6.
 */
@Data
public class UserReportDto implements BaseEntity {

    private long uid;

    private long cid;

    private int reason;

    private String attachment;
}
