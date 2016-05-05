package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/5
 * Time :17:25
 */
@Data
public class ShowUserFollowDto  implements BaseEntity{

    private List<UserFollowDto> result = Lists.newArrayList();
}
