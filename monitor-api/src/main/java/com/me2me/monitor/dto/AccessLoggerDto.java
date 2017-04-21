package com.me2me.monitor.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
import java.util.Date;

/**
 * Created by pc329 on 2017/4/21.
 */
@Data
public class AccessLoggerDto implements BaseEntity {

    private String headers;

    private String uri;

    private String method;

    private long uid;

    private String params;

}
