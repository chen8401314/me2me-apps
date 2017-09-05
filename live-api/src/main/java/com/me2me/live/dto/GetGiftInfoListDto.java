package com.me2me.live.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 *  Author: chenxiang 
 *  Date: 2017-09-05
 */
@Data
public class GetGiftInfoListDto implements BaseEntity {

	private List<GiftInfoElement> result = Lists.newArrayList();

	public static GiftInfoElement createGiftInfoElement() {
		return new GiftInfoElement();
	}

	@Data
	public static class GiftInfoElement implements BaseEntity {
		private static final long serialVersionUID = 9104269954351141321L;

		private Long giftId;

		private String name;

		private String image;

		private Integer price;

		private Integer addPrice;

		private Integer imageWidth;

		private Integer imageHeight;

		private String gifImage;

		private Integer playTime;

		private Integer sortNumber;

		private Integer status;

	}

}
