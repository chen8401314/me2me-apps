package com.me2me.activity.event;

import java.util.List;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ForcedPairingPushEvent implements BaseEntity {

	private static final long serialVersionUID = 7617150165375487175L;

	private List<Long> uidList;
	
	public ForcedPairingPushEvent(List<Long> uidList){
		this.uidList = uidList;
	}
}
