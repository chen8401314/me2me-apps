package com.me2me.live.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017/8/7
 * Time :10:26
 */
@Data
public class GetLotteryDto implements BaseEntity {


    private long uid;

    private String avatar;
    
    private String avatarFrame;

    private String nickName;
    
    private int v_lv;

    private int level;
    
    private long createTime;
    
    private String title;
    
    private String summary;

    private int status;

    private int signUp;
    
    private int isWin;
    
    private long timeInterval;
    
    private long endTime;
    
    private int winNumber;
    
    private int joinNumber;
    
    private long topicId;
    
    
    private List<UserElement> winUsers = Lists.newArrayList();
    @Data
    public static class UserElement implements BaseEntity{
		private static final long serialVersionUID = 986248317266716695L;
		
		private long uid;
		private String avatar;
		private String avatarFrame;
		private String nickName;
		private int v_lv;
		private int level;
    }
}
