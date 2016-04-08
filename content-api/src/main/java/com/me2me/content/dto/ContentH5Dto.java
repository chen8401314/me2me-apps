package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/30
 * Time :17:49
 */
@Data
public class ContentH5Dto implements BaseEntity{

    private String coverImage;

    private List<String> imageUrls = Lists.newArrayList();

    private String content;

    private int type;

    private String title;


}
