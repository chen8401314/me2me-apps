package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import com.me2me.user.model.Dictionary;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :22:05
 */
@Data
public class BasicDataSuccessDto implements BaseEntity {

    private List<BasicDataSuccessElement> results = Lists.newArrayList();

    public static BasicDataSuccessElement createElement(){
        return new BasicDataSuccessElement();
    }

    @Data
    public static class BasicDataSuccessElement implements BaseEntity {

        private long tid;

        private String type;

        private List<Dictionary> list;
    }
}
