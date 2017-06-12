package com.me2me.user.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class FollowEvent implements BaseEntity {

	private static final long serialVersionUID = -2605247864101956237L;
	
	private long sourceUid;
	private long targetUid;
	
	public FollowEvent(long sourceUid, long targetUid){
		this.sourceUid = sourceUid;
		this.targetUid = targetUid;
	}
}
