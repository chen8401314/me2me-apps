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
public class CreateActivityNoticeRequest extends Request {


    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String activityNoticeTitle;

    @Getter
    @Setter
    private String activityNoticeCover;

    @Getter
    @Setter
    private String activityResult;





}
