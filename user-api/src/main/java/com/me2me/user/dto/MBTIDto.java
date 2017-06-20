package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品 Author: 代宝磊 Date: 2016/7/27 Time :23:03
 */
@Data
public class MBTIDto implements BaseEntity {

	private static final long serialVersionUID = 1L;
	private String mbti;
	private boolean isShared;
	private boolean isTested;
	private long kingdomId;
	private long uid;
	private String nickName;
	private String avatar;
	private int vLv;
	private int level;
}
