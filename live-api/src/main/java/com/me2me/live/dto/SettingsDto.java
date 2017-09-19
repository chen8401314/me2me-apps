package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: 马秀成
 * Date: 2017/2/4
 */
@Data
public class SettingsDto implements BaseEntity {

    private long topicId;

    private String coverImage;

    private String title;

    private int readCount;

    private int favoriteCount;

    private int topicCount;

    private long createTime;

    private String summary;

    private int ceCount;

    private int acCount;

    private int pushType;

    private int ceAuditType;

    private int acAuditType;

    private int acPublishType;

    private String tags;

    private String recTags;
    
    private int kcid;
    
    private String kcImage;
    
    private String kcName;
    
    private String kcIcon;

}
