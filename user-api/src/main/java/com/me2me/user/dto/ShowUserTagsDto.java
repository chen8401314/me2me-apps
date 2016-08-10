package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/12
 * Time :16:51
 */
@Data
public class ShowUserTagsDto  implements BaseEntity{

    private List<ShowUserTagElement> showTags = Lists.newArrayList();

    public static ShowUserTagElement createElement(){return  new ShowUserTagElement();}

    @Data
    public static class ShowUserTagElement implements BaseEntity {

        private long uid;

        private String tag;

        private long likeCount;
    }

}
