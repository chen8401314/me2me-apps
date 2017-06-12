package com.me2me.user.event;

import java.util.Map;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class PushExtraEvent implements BaseEntity {
	private static final long serialVersionUID = -6511611855551851188L;
	
	private String uid;
	private String message;
	private Map<String,String> extraMaps;
}
