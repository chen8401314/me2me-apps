package com.me2me.live.dto;

import java.util.Date;
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
public class VoteInfoDto implements BaseEntity {

    private long uid;

    private String avatar;
    
    private String nickName;

    private int v_lv;

    private int level;
    
    private String myVote;
    
    private long voteId;
    
    private String title;
    
    private int type;
    
    private int status;
    
    private int canEnd;

    private int recordCount;
    
    private Date createTime;
    
    private  List<OptionElement> options = Lists.newArrayList();
    
    private  List<UserElement> users = Lists.newArrayList();
    
    public static OptionElement createOptionElement(){
        return new OptionElement();
    }

    @Data
    public static class OptionElement implements BaseEntity {

        private long id;

        private String option;

        private int count;


    }
    
    public static UserElement createUserElement(){
        return new UserElement();
    }

    @Data
    public static class UserElement implements BaseEntity {

        private long uid;

        private String avatar;
        
        private String nickName;

        private int v_lv;


    }
}
