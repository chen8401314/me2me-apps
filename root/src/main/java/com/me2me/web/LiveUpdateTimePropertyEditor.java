package com.me2me.web;

import org.springframework.beans.propertyeditors.PropertiesEditor;

import java.util.Date;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/14.
 */
public class LiveUpdateTimePropertyEditor extends PropertiesEditor {

    @Override
    public String getAsText() {
        return getValue().toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Long value = Long.valueOf(text);
        Date date = new Date(value);
        setValue(date);
    }
}
