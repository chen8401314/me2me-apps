package com.me2me.search.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 推荐用户。带推荐原因
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
@Data
public class RecommendKingdom implements BaseEntity{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private long uid;
	private String nickName;
	private String avatar;
	private int isFollowed;
	private int isFollowMe;
	private String introduced;
	private int v_lv;
	
	private String title;
	private long topicId;
	private String cover;
	private String tags;
	private String  reason;
}
