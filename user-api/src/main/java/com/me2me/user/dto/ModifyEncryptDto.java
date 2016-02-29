package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :10:07
 */
@Data
public class ModifyEncryptDto implements BaseEntity{


    private String userName;

    private String oldEncrypt;

    private String firstEncrypt;

    private String secondEncrypt;

}
