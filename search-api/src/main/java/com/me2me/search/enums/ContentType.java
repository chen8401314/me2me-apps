package com.me2me.search.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>内容介质类型.</p>
 * @author carl
 */
public enum ContentType {
    ALL("_all", "所有"),
    ARTICLE("文章", "文章"),
    MUSIC("音乐", "音乐"),
    VIDEO("视频", "视频|电影|电视剧"),
    PERSONA("用户画像","用户画像"),
    KINGDOM("王国", "王国内容"),
    UGC("UGC", "UGC、PGC内容"),
    APPLICATION("应用","应用");

    /** 代码 */
    private final String  code;
    /** 信息 */
    private final String  message;

    private ContentType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过代码获取ENUM
     * @param code
     * @return
     */
    public static ContentType getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        for (ContentType type : ContentType.values()) {
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
