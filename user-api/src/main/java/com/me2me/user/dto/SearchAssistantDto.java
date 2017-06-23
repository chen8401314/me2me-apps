package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/13.
 */
@Data
public class SearchAssistantDto implements BaseEntity {


    private List<SearchAssistantElement> result = Lists.newArrayList();

    public SearchAssistantElement createElement(){
        return new SearchAssistantElement();
    }

    @Data
    public class SearchAssistantElement implements BaseEntity{

        private long uid;

        private String nickName;

        private String avatar;

        private int v_lv;

        private int level;

    }

}
