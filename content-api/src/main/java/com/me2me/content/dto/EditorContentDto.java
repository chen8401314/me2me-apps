package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/26.
 */
@Data
public class EditorContentDto implements BaseEntity {

    private int type;

    private int page = 1;

    private int pageSize = 10;

    private int articleType;

    private String keyword;

}
