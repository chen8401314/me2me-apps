package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :11:22
 */
@Data
public class TagDetailDto  implements BaseEntity {

	
    private List<Object> tagKingdomList = Lists.newArrayList();
	
    private long tagId;
    
    private String tagName;
    
    private int isLike;
	
    
    private List<CoverElement> coverList = Lists.newArrayList();
    //内容
    @Data
    public static class MyPublishElement extends BaseContentDto implements BaseEntity{
    	
        private List<ReviewElement> reviews = Lists.newArrayList();

        private List<OutDataElement> textData = Lists.newArrayList();
        private List<OutDataElement> audioData = Lists.newArrayList();
        private List<OutDataElement> imageData = Lists.newArrayList();
        private List<OutDataElement> ugcData = Lists.newArrayList();
        
        public static ReviewElement createReviewElement(){
            return new ReviewElement();
        }

        @Data
        public static class ReviewElement implements BaseEntity{

            private long uid;

            private String nickName;

            private String avatar;

            private int level;

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
    
    @Data
    public static class AdElement implements BaseEntity {
    	
		private static final long serialVersionUID = -6240068377784693096L;
		private long cid;
		private int type;
		private int h;
		private int w;
    }
    @Data
    public static class CoverElement implements BaseEntity {
		private static final long serialVersionUID = 2901955620229846018L;
		private long cid;
		private long topicId;
		private String coverImage;
		private String title;
		private int type;
    }
}
