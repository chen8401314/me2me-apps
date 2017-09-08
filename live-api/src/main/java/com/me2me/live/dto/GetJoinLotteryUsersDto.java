package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017/8/7
 * Time :10:26
 */
@Data
public class GetJoinLotteryUsersDto implements BaseEntity {
	
    private List<UserElement> joinUsers = Lists.newArrayList();
    @Data
    public static class UserElement implements BaseEntity{
		private static final long serialVersionUID = 916248317266716695L;
		private long sinceId;
		private long uid;
		private String avatar;
		private String avatarFrame;
		private String nickName;
		private int v_lv;
		private int level;
		private String content;
		private int prohibit;
		private long createTime;
    }
}
