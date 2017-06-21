package com.me2me.user.rule;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/6/16 0016.
 */
@Data
public class CoinRule implements BaseEntity {

    // 标识
    private int code;

    // 规则名称
    private String name;

    // 上限
    private int point;

    // 是否重复计算
    private boolean repeatable;

    //拓展字段
    private long ext;

    public CoinRule(int code, String name, int point, boolean repeatable) {
        this.code = code;
        this.name = name;
        this.point = point;
        this.repeatable = repeatable;

    }
}
