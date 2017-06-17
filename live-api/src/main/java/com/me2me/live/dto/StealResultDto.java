package com.me2me.live.dto;

import java.util.ArrayList;
import java.util.List;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/13
 * Time :19:22
 */
@Data
public class StealResultDto implements BaseEntity{
	private static final long serialVersionUID = 6369649571729593474L;

	private int stealedCoins;
    private int upgrade;
    private CurrentLevel currentLevel;
    @Data
    public static class CurrentLevel implements BaseEntity{
		private static final long serialVersionUID = 1L;
		private int level;
    	private String name;
    	private List<Permission> permissions=new ArrayList<>();
    	
    }
    @Data
	public static class Permission  implements BaseEntity{
		private String name;
	}
}
