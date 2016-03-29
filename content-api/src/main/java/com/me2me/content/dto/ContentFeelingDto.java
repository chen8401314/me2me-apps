package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;
import sun.plugin.util.UserProfile;

import java.util.AbstractList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/28
 * Time :20:13
 */
@Data
public class ContentFeelingDto implements BaseEntity {

    private List<ContentFeelingElement> result = Lists.newArrayList();

    public static ContentFeelingElement createElement(){
        return new ContentFeelingElement();
    }

    @Data
    public static class ContentFeelingElement implements BaseEntity{

        private String feeling;

        private int likeCount;

        private List<UserProfile> likeUsers;



    }
}
