package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ImageInfoDTO implements BaseEntity {
	private static final long serialVersionUID = -5080585121896543134L;

	private int likeCount;
	private int isLike;
}
