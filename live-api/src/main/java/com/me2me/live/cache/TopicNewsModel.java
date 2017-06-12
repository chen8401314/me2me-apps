package com.me2me.live.cache;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-6-8
 * Time :10:12
 */
@Data
public class TopicNewsModel implements BaseEntity {

    private static final String KEY_PREFIX = "topic:news:";

    /**
     * 用户跑马灯是否读取模型
     * 该结构采用hashmap
     * redis key 命名规则为 topicNewsId
     * field 采用fragment的id
     * value 默认值为0
     */

    private String key;

    private String field;

    private String value;

    public TopicNewsModel(long topicNewsId,long uid,String value){
        this.key = KEY_PREFIX+topicNewsId ;
        this.field = uid + "";
        this.value = value;
    }
}
