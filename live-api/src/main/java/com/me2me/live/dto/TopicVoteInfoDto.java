package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-05-10
 */
@Data
public class TopicVoteInfoDto implements BaseEntity {


    private long voteId;

    private String title;

    private int type;
    
    private int status;

    private  List<OptionElement> options = Lists.newArrayList();
    
    public static OptionElement createElement(){
        return new OptionElement();
    }

    @Data
    public static class OptionElement implements BaseEntity {

        private long id;

        private String option;

        private int count;


    }
}
