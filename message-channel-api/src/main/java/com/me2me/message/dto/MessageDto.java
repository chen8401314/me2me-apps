package com.me2me.message.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/7/8.
 */
@Data
public class MessageDto implements BaseEntity {

    private int messageType;

    private String body;

}
