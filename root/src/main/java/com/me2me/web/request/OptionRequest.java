package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/2/29
 * Time :15:44
 */
public class OptionRequest extends Request{

    // pgc 1
    // ugc 0
    // 活动 2

    @Getter
    @Setter
    private int optionAction;

    // 上架，置热为 1 否则为0
    @Getter
    @Setter
    private int action;

    @Getter
    @Setter
    private long id;







}
