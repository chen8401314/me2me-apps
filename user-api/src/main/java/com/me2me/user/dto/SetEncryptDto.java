package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/7/14 0014.
 */
@Data
public class SetEncryptDto implements BaseEntity  {

    private String userName;

    private String encrypt;

}
