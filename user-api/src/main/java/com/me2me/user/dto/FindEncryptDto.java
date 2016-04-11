package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/24
 * Time :18:07
 */
@Data
public class FindEncryptDto implements BaseEntity {

    private String userName;

    private String firstEncrypt;

    private String secondEncrypt;

}
