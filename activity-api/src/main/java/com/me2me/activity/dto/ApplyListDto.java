package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;
import sun.misc.BASE64Decoder;

import java.util.List;

/**
 * Created by 马秀成 on 2016/12/9.
 */
@Data
public class ApplyListDto implements BaseEntity {

    private List<ApplyElement> sendList = Lists.newArrayList();

    private List<ApplyElement> agreeList = Lists.newArrayList();

    private List<ApplyElement> receiveList = Lists.newArrayList();

    public ApplyElement createApplyElement(){
        return new ApplyElement();
    }

    private int total;//总记录 接收到的

    @Data
    public class ApplyElement implements BaseEntity {

        private long uid;

        private int vLv;

        private String nickName;

        private long id; //此id为了实现双人王国申请操作接口

        private String avatar;

        private int status; //1申请中，2已配对，3可创建，4已撤销。

        private String title;

        private long topicId;
    }

}
