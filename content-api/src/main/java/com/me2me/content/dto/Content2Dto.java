package com.me2me.content.dto;

import com.me2me.content.model.Content;
import lombok.Data;

import java.util.Comparator;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/23
 * Time :14:48
 */
@Data
public class Content2Dto extends Content{

    private long hid;


    // 操作时间排序使用
    private Long operationTime;


}
