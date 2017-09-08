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

	private int hasNextNew;//0没有新消息，1有新消息
	
    private List<UserNoticeElement> userNoticeList = Lists.newArrayList();

    @Data
    public static class UserNoticeElement implements BaseEntity {

        private long id;

        private int noticeType;

        private long fromUid;

        private long toUid;

        private String fromNickName;

        private String toNickName;

        private String tag;

        private String coverImage;

        private String summary;

        private int likeCount;

        private String fromAvatar;
        
        private String fromAvatarFrame;

        private int readStatus;

        private Date createTime;

        private long cid;

        private String review;

        private int v_lv;

        private int level;

        private int to_v_lv;
        
        //0圈外 1圈内 2核心圈(当消息对象为王国时有效)
        private int internalStatus;
        
        //0圈外 1圈内 2核心圈(当消息对象为王国时有效)
        private int fromInternalStatus;
        
        private int contentType;//王国类型，0个人王国，1000聚合王国，只有noticeType=3、4、6、7时有效
        
        private long applyId;//申请ID
        private int applyStatus;//0初始，1同意，2拒绝，3失效
        
        //以下为聚合相关新增的参数
        private int coverType;
        private long coverTopicId;
        private String coverTitle;
        
        private String textImage;
        private String textTitle;
        private long textTopicId;
        private int textType;
        private int textInternalStatus;
        
    }



}
