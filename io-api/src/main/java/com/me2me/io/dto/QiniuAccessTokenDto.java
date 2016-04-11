package com.me2me.io.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/24.
 */
@Data
public class QiniuAccessTokenDto implements BaseEntity {

    private String token;

    private long expireTime;

}
