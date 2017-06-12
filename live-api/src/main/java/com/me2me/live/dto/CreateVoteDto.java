package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017-05-8
 */
@Data
public class CreateVoteDto implements BaseEntity {

    private long topicId ;

    private int source;

    private String title;
    
    private String option;
    
    private long uid;
    
    private int type;

}
