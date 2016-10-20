package com.me2me.live.cache;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/24
 * Time :15:56
 */
@Data
public class LiveLastUpdate implements BaseEntity {
    private static final String KEY_PREFIX = "live:last:update:";

    /**
     * 我订阅的直播是否有更新缓存模型
     * 该结构采用hashmap
     * redis key 命名规则为 my:livesStatus:uid
     * field 采用订阅直播的id
     * value 默认值为0，若直播有更新则为1
     */

    private String key;

    private String field;

    private String value;

    public LiveLastUpdate(long topicId,String value){
        this.key = KEY_PREFIX ;
        this.field = topicId + "";
        this.value = value;
    }
}
