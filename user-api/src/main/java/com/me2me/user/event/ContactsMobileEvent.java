package com.me2me.user.event;

import java.util.List;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ContactsMobileEvent implements BaseEntity {
	private static final long serialVersionUID = -7522097042855987564L;

	private long uid;
	private List<String> mobileList;
}
