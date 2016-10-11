package com.me2me.mgmt.dal.entity;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class MgmtUser implements BaseEntity {

	private static final long serialVersionUID = 1420856437648887862L;

	private long id;
	private String uuid;
	private String name;
	private String pwd;
	private int type = 2;
	private String appUid;
	private String appToken;
}
