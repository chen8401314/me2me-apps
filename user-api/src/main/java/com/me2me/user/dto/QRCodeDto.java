package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/2
 * Time :18:43
 */
@Data
public class QRCodeDto implements BaseEntity{

    private String  qrCodeUrl;
}
