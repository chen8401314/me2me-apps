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
public class SearchDto implements BaseEntity {

    private List<SearchElement> result = Lists.newArrayList();

    private int totalPage;

    private int totalRecord;

    public SearchElement createElement(){
        return new SearchElement();
    }

    @Data
    public class SearchElement implements BaseEntity{

        private long uid;

        private String nickName;

        private String avatar;

        private int isFollowed;

        private int isFollowMe;

        private String introduced;

        private int v_lv;

    }

}
