package com.me2me.search.dto;

import lombok.Data;

/**
 * 推荐用户。带推荐原因
 * @author zhangjiwei
 * @date Apr 20, 2017
 */
@Data
public class RecommendKingdom extends BaseKingdomInfo{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int  recommendReason;
}
