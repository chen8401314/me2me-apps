package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/5/3
 * Time :16:31
 */
@Data
public class ShowAttentionDto implements BaseEntity {

    private List<ContentElement> attentionData = Lists.newArrayList();

    public static ContentElement createElement(){
        return new ContentElement();
    }


    @Data
    public static class ContentElement extends BaseContentDto implements BaseEntity {

        private List<ReviewElement> reviews = Lists.newArrayList();

        private List<OutDataElement> textData = Lists.newArrayList();
        private List<OutDataElement> audioData = Lists.newArrayList();
        private List<OutDataElement> imageData = Lists.newArrayList();
        private List<OutDataElement> ugcData = Lists.newArrayList();
        
        public static ReviewElement createElement(){
            return new ReviewElement();
        }

        @Data
        public static class ReviewElement implements BaseEntity{

            private long uid;

            private String nickName;

            private String avatar;

            private Date createTime;

            private String review;

        }
    }
    
    @Data
    public static class OutDataElement implements BaseEntity {
		private static final long serialVersionUID = -1680174794247251182L;
    	
		private long id;
		private int contentType;
		private int type;
		private String fragment;
		private String fragmentImage;
		private long atUid;
		private String atNickName;
		private String extra;
    }
}
