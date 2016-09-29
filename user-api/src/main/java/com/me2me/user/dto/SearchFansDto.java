package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/18
 * Time :17:50
 */
@Data
public class SearchFansDto implements BaseEntity {

    private String nickName;

    private int start;

    private int pageSize;

    private long uid;

}
