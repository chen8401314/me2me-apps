package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Data
public class UserNoticeDto implements BaseEntity {
	private static final long serialVersionUID = 4823843638329755669L;

	private long uid;

    private long sinceId ;

    private int level;
}
