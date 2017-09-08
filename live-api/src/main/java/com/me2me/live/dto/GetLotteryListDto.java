package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-08-7
 */
@Data
public class GetLotteryListDto implements BaseEntity {

    
	
    private  List<LotteryInfoElement> lotteryList = Lists.newArrayList();
    
    
    public static LotteryInfoElement createLotteryInfoElement(){
        return new LotteryInfoElement();
    }

    @Data
    public static class LotteryInfoElement implements BaseEntity {
    	private static final long serialVersionUID = 9104169954351141021L;
    	
        private long sinceId;
        
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
        
        private long uid;
        
        private List<WinUser> winUsers = Lists.newArrayList();;
      
        
    }
    
    public static WinUser createWinUser(){
        return new WinUser();
    }

    @Data
    public static class WinUser implements BaseEntity {

    	private static final long serialVersionUID = 9104369954351141721L;
    	
        private long uid;

        private String avatar;
        
        private String avatarFrame;
        
        private String nickName;

        private int v_lv;
        
        private int level;
        
        
    }
}
