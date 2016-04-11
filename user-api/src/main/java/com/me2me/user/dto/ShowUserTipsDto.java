package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.me2me.user.model.UserTips;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/24
 * Time :18:07
 */
@Data
public class ShowUserTipsDto implements BaseEntity {

    private List<UserTips> tips = Lists.newArrayList();

}
