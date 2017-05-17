package com.me2me.user.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ShowNoticeReddotQueryDTO implements BaseEntity {
	private static final long serialVersionUID = 704814015801282870L;

	private int unreadCount;//通知红点个数
	private int contactReddot;//通讯录红点
}
