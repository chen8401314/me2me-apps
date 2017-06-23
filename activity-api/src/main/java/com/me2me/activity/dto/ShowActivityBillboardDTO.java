package com.me2me.activity.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowActivityBillboardDTO implements BaseEntity {
	private static final long serialVersionUID = 154057696658415958L;

	private UserElement myUser = new UserElement();

	private List<UserElement> userData = Lists.newArrayList();
	
	private List<AreaElement> areaData = Lists.newArrayList();
	
	@Data
	public static class UserElement implements BaseEntity {
		private static final long serialVersionUID = 3248628725211037883L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private int v_lv;
		private int level;
		private long score;
		private int rank;
	}
	
	@Data
	public static class AreaElement implements BaseEntity {
		private static final long serialVersionUID = -7964215784497882431L;
		
		private long topicId;
		private String name;
		private long score;
		private int rank;
	}
}
