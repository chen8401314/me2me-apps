package com.me2me.mgmt.dal.entity;

import java.util.Date;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class MgmtSysLog implements BaseEntity {

	private static final long serialVersionUID = 8877992989071123373L;

	private long id;
	private String description;
	private String requestPath;
	private String method;
	/** 0：操作日志 */
	private int type;
	private String ip;
	private long userId;
	private String userName;
	private Date createTime;
	
}
