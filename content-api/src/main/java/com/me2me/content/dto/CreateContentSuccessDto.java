package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/6
 * Time :11:20
 */
@Data
public class CreateContentSuccessDto  implements BaseEntity{

    private String feeling;

    private String content;

    private String coverImage;

    private int contentType;

    private long forwardCid;

    private int type;

    private long uid;

    private long id;

    private Date createTime;

    private long tid;

    private String forwardTitle;

    private String forwardUrl;

    private int v_lv;

    private int upgrade;

    private int currentLevel;
    
    private long topicId;
    private int internalStatus;
    private long fragmentId;
}
