package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/22.
 */
@Data
public class SquareDataDto implements BaseEntity {

    /**
     * 该版本广场规则
     * 1. 显示原生UGC和转发系统（图文|音乐）
     *
     */
    private List<SquareDataElement> results = Lists.newArrayList();

    public static SquareDataElement createElement(){
        return new SquareDataElement();
    }

    @Data
    public static class SquareDataElement extends BaseContentDto implements BaseEntity{

        private int contentType;
        
    }

}
