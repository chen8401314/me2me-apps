package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by 马秀成 on 2016/12/8.
 */
@Data
public class BlurSearchDto implements BaseEntity {

    private long id;

    private long topicId;

    private String title;

    private String liveImage;

    private String nickName;

    private long uid;

    private int gender;

    private String avatar;

    private int isAlone;//是否单身 0单身 1不是单身

    private long hot;

    private String nickName2; //发起人

}
