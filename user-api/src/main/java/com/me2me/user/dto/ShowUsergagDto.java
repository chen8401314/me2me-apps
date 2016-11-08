package com.me2me.user.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowUsergagDto implements BaseEntity {

	private static final long serialVersionUID = -6329197289426558681L;

	private int totalPage;

    private int totalRecord;
	
	private List<UsergagElement> result = Lists.newArrayList();
	
	public static UsergagElement createUsergagElement(){
		return new UsergagElement();
	}
	
	@Data
	public static class UsergagElement implements BaseEntity{

		private static final long serialVersionUID = 8968756748203074192L;
		
		private long id;
		private long targetUid;
		private long uid;
		private int type;
		private long cid;
		private int gagLevel;
		
		private String targetUserName;
	}
}
