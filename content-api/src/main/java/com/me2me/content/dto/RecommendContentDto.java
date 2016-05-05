package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.plusnet.search.content.domain.ContentTO;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Data
public class RecommendContentDto implements BaseEntity {

    private List<ContentTO> result = Lists.newArrayList();



}
