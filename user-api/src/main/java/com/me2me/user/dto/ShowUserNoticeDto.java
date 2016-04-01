package com.me2me.user.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/1.
 */
@Data
public class ShowUserNoticeDto implements BaseEntity {

    private List<UserNoticeElement> results = Lists.newArrayList();


    public UserNoticeElement createUserNoticeElement(){
        return new UserNoticeElement();
    }

    @Data
    private class UserNoticeElement implements BaseEntity{

        /**
         * 点赞人或贴标人的UID
         */
        private String uid;

        /**
         * 点赞人或贴标人的头像
         */
        private String avatar;

        /**
         * 标签
         */
        private String feelTag;

        /**
         * 点赞人或贴标人的昵称
         */
        private String nickName;

        /**
         * 时间
         */
        private Date time;

        /**
         * 内容图片
         */
        private String blockImage;

        /**
         * 内容文本
         */
        private String blockText;



    }

}
