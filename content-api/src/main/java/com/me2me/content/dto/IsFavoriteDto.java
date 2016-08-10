package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/26
 * Time :14:11
 */
@Data
public class IsFavoriteDto implements BaseEntity {

    private long topicId;

    private long uid;
}
