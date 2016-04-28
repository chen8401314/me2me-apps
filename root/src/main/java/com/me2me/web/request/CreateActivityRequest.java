package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/27.
 */
public class CreateActivityRequest extends Request {


    // 活动期次
    @Getter
    @Setter
    private String issue;

    // 活动标题
    @Getter
    @Setter
    private String title;

    // 活动标识
    @Getter
    @Setter
    private String hashTitle;

    // 活动内容
    @Getter
    @Setter
    private String content;

    // 活动封面图
    @Getter
    @Setter
    private String cover;

    // 活动通知
    @Getter
    @Setter
    private String notice;

    // 活动开始时间
    @Getter
    @Setter
    private Date startTime;

    // 活动结束时间
    @Getter
    @Setter
    private Date endTime;





}
