package com.me2me.search.dto;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>内容归类.</p>
 * 
 * @author carl
 */
public enum ContentGrouping {
    ARTICLE("文章", "文章"),//做兼容
    READING("阅读", "阅读"),
    PARTY("社交", "社交"),
    ENTERTAINMENT("娱乐", "休闲娱乐"),
    EAT("吃", "吃"),
    RESIDE("住", "住"),
    TRANSPORT("行", "行"),
    TRAVEL("游", "游玩"),
    SHOPPING("购物", "购物"),
    BEAUTY("丽人", "丽人"),
    SPORTS("运动", "运动健身"),
    LIVE("生活服务", "生活服务"),
    EDUCATION("教育", "学习教育培训"),
    MEDICAL("医疗", "医疗健康"),
    WORK("工作", "办公工作"),
    APPLICATION("应用", "应用");

    /** 代码 */
    private final String  code;
    /** 信息 */
    private final String  message;

    private ContentGrouping(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过代码获取ENUM
     * @param code
     * @return
     */
    public static ContentGrouping getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        for (ContentGrouping type : ContentGrouping.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }

        return null;
    }
    
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
