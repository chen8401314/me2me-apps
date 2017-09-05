package com.me2me.mgmt.request;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Lists;

public class AppUserQueryDTO {

	@Getter
    @Setter
	private String nickName;
	@Getter
    @Setter
	private int isV;
	@Getter
    @Setter
	private String mobile;
	@Getter
    @Setter
	private int status;//0：全部 1：正常 2：失效
	@Getter
    @Setter
	private String startTime;
	@Getter
    @Setter
	private String endTime;
	@Getter
    @Setter
	private String meCode;
	@Getter
    @Setter
	private int yunying;
	
	@Getter
    @Setter
	private List<UserProfileElement> result = Lists.newArrayList();
	
	@Data
	public static class UserProfileElement {
		
		private long uid;
		private String mobile;
		private String nickName;
		private int gender;
		private String avatar;
		private String birthday;
		private String thirdPartBind;
		private int vlv;
		
		private Date createTime;
		private int status;
		
		private long meCode;
		private int level;
		private int excellent;
	}
}
