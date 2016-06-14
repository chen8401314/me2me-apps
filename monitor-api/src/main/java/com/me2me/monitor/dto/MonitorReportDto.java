package com.me2me.monitor.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/14.
 */
@Data
public class MonitorReportDto implements BaseEntity {

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 截止日期
     */
    private String endDate;


    /**
     * 类型
     */
    private int type;

    /**
     * 渠道
     */
    private int channel;

    /**
     * 行为类型
     */
    @Getter
    @Setter
    private int actionType;

}
