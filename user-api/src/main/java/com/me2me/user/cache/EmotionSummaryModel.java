package com.me2me.user.cache;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-05-25
 * Time :10:12
 */
@Data
public class EmotionSummaryModel implements BaseEntity {

    private static final String KEY_PREFIX = "emotion:summary:";

    /**
     * 用户是否周总结状态缓存模型
     * 该结构采用hashmap
     * redis key 命名规则为 emotion:summary:date
     * field 采用fragment的id
     * value 默认值为0
     */

    private String key;

    private String field;

    private String value;

    public EmotionSummaryModel(String date,long uid,String value){
        this.key = KEY_PREFIX+date ;
        this.field = uid + "";
        this.value = value;
    }
}
