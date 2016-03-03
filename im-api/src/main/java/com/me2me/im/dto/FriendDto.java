package com.me2me.im.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Data
public class FriendDto implements BaseEntity {

    public List<FriendElement> result = new ArrayList<FriendElement>();


    public FriendElement createFriendElement(){
        return new FriendElement();
    }

    @Data
    public class FriendElement implements BaseEntity{

        private long fid;

        private String avatar;

        private String nickName;
    }
}
