package com.me2me.search.dto;

import java.util.List;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 推荐用户。带推荐原因
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
@Data
public class RecommendUser implements BaseEntity{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private long uid;
	private String nickName;
	private String avatar;
	private int v_lv;
	private int level;
	private String avatarFrame;
	private String reason;
	private List<String> userTags;		// 用户标签
	private int tagMatchedLength;	// 匹配标签长度
	private int matching;//匹配度
}
