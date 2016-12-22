package com.me2me.activity.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Created by 马秀成 on 2016/12/12.
 */
@Data
public class BridListDto implements BaseEntity {

    private int total;

    private List<ApplyElement> bridList = Lists.newArrayList();

    public ApplyElement createApplyElement(){
        return new ApplyElement();
    }

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
