package com.me2me.live.cache;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-05-10
 * Time :10:12
 */
@Data
public class TeaseAutoPlayStatusModel implements BaseEntity {

    private static final String KEY_PREFIX = "tease:autoPlayStatus:";

    /**
     * 逗一逗自动播放状态缓存模型
     * 该结构采用hashmap
     * redis key 命名规则为 tease:autoPlayStatus:fragmentId
     * field 采用fragment的id
     * value 默认值为0，若已自动播放过则为1
     */

    private String key;

    private String field;

    private String value;

    public TeaseAutoPlayStatusModel(long fragmentId,String value){
        this.key = KEY_PREFIX ;
        this.field = fragmentId + "";
        this.value = value;
    }
}
