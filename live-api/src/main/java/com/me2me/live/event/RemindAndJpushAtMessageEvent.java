package com.me2me.live.event;

import lombok.Data;

import com.me2me.common.web.BaseEntity;
import com.me2me.live.dto.SpeakDto;

@Data
public class RemindAndJpushAtMessageEvent implements BaseEntity {
	private static final long serialVersionUID = 2732806807005325652L;

	private SpeakDto speakDto;
}
