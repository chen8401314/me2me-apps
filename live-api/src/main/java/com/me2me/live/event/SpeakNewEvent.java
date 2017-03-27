package com.me2me.live.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class SpeakNewEvent implements BaseEntity {
	private static final long serialVersionUID = -2515279985958302669L;

	private long topicId = 0;
    private int type = 0;
    private long uid = 0;
    private long atUid = 0;
    private long fragmentId = 0;
}
