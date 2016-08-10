package com.me2me.sms.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/31
 * Time :13:45
 */
@Data
public class PushMessageIosDto implements BaseEntity {

    private String title;

    private String content;

    private String token;

    private int messageType;

}
