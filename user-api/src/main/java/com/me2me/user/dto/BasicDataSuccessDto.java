package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import com.me2me.user.model.Dictionary;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :22:05
 */
@Data
public class BasicDataSuccessDto implements BaseEntity {

    private Map<Long,List<Dictionary>> result;
}
