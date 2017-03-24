package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/8
 * Time :10:31
 */
@Data
public class LiveQRCodeDto implements BaseEntity{

    private String  liveQrCodeUrl;

    private String summary;

}
