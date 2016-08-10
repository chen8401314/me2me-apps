package com.me2me.common.utils;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/21.
 */
public class Lists {

    /**
     * 获取单个结果对象
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T  getSingle(List<T> list){
        if(list!=null && list.size() > 0){
           return list.get(0);
        }
        return null;
    }

}
