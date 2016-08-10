package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/5/5.
 */
@Data
public class ShowUGCDetailsDto implements BaseEntity {

    private long id;

    private String cover;

    private String images;

    private String content;

    private String feelings;

    private String title;

}
