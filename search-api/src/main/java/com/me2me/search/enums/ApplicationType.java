package com.me2me.search.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>应用类型.</p>
 * @author carl
 */
public enum ApplicationType {
    CRAWLER("crw","网络爬取文章内容"),
    PERSONA("usr","用户画像内容"),
    MUSIC("mus","音乐"),
    VIDEO("vid","视频"),
    KINGDOM("kgd", "王国内容"),
    UGC("ugc", "UGC、PGC内容"),
    TOUR("tou", "旅游内容"),
    OTHER("oth", "未定义内容"); //仅适配用

    /** 代码 */
    private final String  code;
    /** 信息 */
    private final String  message;

    private ApplicationType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过代码获取ENUM
     * @param code
     * @return
     */
    public static ApplicationType getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        for (ApplicationType type : ApplicationType.values()) {
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
