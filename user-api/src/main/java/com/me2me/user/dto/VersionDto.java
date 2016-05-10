package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/10
 * Time :16:42
 */
@Data
public class VersionDto  implements BaseEntity{

    private String version;

    private Integer platform;

    private String updateDescription;

    private String updateUrl;
}
