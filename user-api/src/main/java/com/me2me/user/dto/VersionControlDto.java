package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/10
 * Time :16:21
 */
@Data
public class VersionControlDto implements BaseEntity{

    private long id;

    private String version;

    private String updateDescription;

    private String updateUrl;

    private Date updateTime;

    private int platform;

    private int isUpdate;

    private int resourceCode;//0不是 1是
}
