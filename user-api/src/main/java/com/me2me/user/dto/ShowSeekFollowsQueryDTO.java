package com.me2me.user.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowSeekFollowsQueryDTO implements BaseEntity {
	private static final long serialVersionUID = -2074251863151524140L;

	private int isSeek;
	
	private List<SeekFollowElement> seekFollowData = Lists.newArrayList();
	
	@Data
	public static class SeekFollowElement implements BaseEntity {
		private static final long serialVersionUID = 3368189181971742440L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private String introduced;
		private int v_lv;
		private int isFollowed;
		private int isFollowMe;
	}
}
