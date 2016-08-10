package com.me2me.sms.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/2
 * Time :14:51
 */
@Data
public class PushLogDto implements BaseEntity{

    private int retCode;

    private String content;

    private int messageType;

}
