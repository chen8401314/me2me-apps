package com.me2me.sns.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/27
 * Time :15:42
 */
@Data
public class ShowMembersDto implements BaseEntity {

    private List<UserElement> members = Lists.newArrayList();

    public UserElement createUserElement(){
        return new UserElement();
    }

    @Data
    public static class UserElement implements BaseEntity{

        private long uid;

        private String nickName;

        private String avatar;
        
        private String avatarFrame;

        private String introduced;

        private int internalStatus;

        private int v_lv;

        private int level;
    }
}
