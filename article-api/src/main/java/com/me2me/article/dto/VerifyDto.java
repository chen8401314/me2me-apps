package com.me2me.article.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
@Data
public class VerifyDto implements BaseEntity {


    private int action;

    private String verifyCode;

    private String mobile;

}
