package com.me2me.sns.service.widget;

import com.me2me.common.web.Specification;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :22:07
 */
public class ModifyCircleFactory {

    public static ModifyCircle getInstance(int type){
        ModifyCircle instance = null;

        if (type == Specification.ModifyCircleType.CORE_CIRCLE.index) {

        } else if (type == Specification.ModifyCircleType.IN_CIRCLE.index) {

        } else if (type == Specification.ModifyCircleType.CANCEL_CORE_CIRCLE.index) {

        } else if (type == Specification.ModifyCircleType.CANCEL_IN_CIRCLE.index) {

        }
        return instance;
    }
}
