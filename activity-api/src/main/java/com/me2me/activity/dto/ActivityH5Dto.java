package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/6
 * Time :15:04
 */
@Data
public class ActivityH5Dto implements BaseEntity{

    //活动标题
    private String title;

    //内容
    private String activityContent;

    //活动封面
    private String coverImage;

    private Date publishTime;

    private String nickName;

    private String avatar;




}

