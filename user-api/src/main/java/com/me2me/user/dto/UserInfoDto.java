package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/3/31
 * Time :13:40
 */
@Data
public class UserInfoDto implements BaseEntity{

    private User user = new User();

    private List<ContentElement> contentElementList = Lists.newArrayList();

    public static ContentElement createElement(){
        return new ContentElement();
    }

    @Data
    public static class User implements BaseEntity{

        private String nickName;

        private long uid;

        private String avatar;

        private int gender;

        private String mid;

        private int isFollow;
    }

    @Data
    public static class ContentElement implements BaseEntity{

        private long tid;

        private long cid;

        private String tag;

        private String coverImage;

        private String content ;

        private Date createTime;

        private int likeCount;

        private int reviewCount;

        private int personCount;

        private int hotValue;

        private Long forwardCid;

        private Integer type;

        private String forwardTitle;

        private String forwardUrl;

        private Integer contentType;

        private String thumbnail;

        private Integer authorization;




    }
}
