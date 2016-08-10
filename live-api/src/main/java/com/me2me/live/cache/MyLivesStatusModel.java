package com.me2me.live.cache;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/4
 * Time :10:12
 */
@Data
public class MyLivesStatusModel implements BaseEntity {

    private static final String KEY_PREFIX = "my:livesStatus:";

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

    public MyLivesStatusModel(long uid,String value){
        this.key = KEY_PREFIX ;
        this.field = uid + "";
        this.value = value;
    }
}
