package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/11
 * Time :18:23
 */
@Data
public class CreateLiveDto implements BaseEntity {

    private long uid ;

    private String title;

    private String liveImage;

}
