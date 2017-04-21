package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowAcommonChatQueryDTO implements BaseEntity {
	private static final long serialVersionUID = 8169293749801056584L;

	private List<ChatElement> result = Lists.newArrayList();
	
	@Data
	public static class ChatElement implements BaseEntity {
		private static final long serialVersionUID = 4889942518696533213L;
		
		private String nickName;
		private String avatar;
		private String message;
		private int type;
		private long sinceId;
	}
}
