package com.me2me.user.event;

import java.util.List;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class BatchFollowEvent implements BaseEntity {
	private static final long serialVersionUID = 638889105166154793L;

	private long sourceUid;
	private List<Long> targetUids;
}
