package com.me2me.sns.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/27
 * Time :15:41
 */
@Data
public class ShowMemberConsoleDto implements BaseEntity {

    private int members;

    private int coreCircleMembers;

    private int inCircleMembers;

    private int outCircleMembers;

    private List<UserElement> coreCircle = Lists.newArrayList();

    private List<UserElement> inCircle = Lists.newArrayList();

    private List<UserElement> outCircle = Lists.newArrayList();

    public static UserElement createUserElement(){
        return new UserElement();
    }

    @Data
    public static class UserElement implements BaseEntity{

        private long uid;

        private String nickName;

        private String avatar;

        private String introduced;

        private int internalStatus;
    }









}
