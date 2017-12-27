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
 * Time :9:28
 */
@Data
public class ShowAcKingdomDto implements BaseEntity{

    private List<ContentElement> acKingdomList = Lists.newArrayList();

    private List<AcImageElement> acImageList = Lists.newArrayList();
    
    private int acCount;
    
    public static ContentElement createElement(){
        return new ContentElement();
    }


    @Data
    public static class ContentElement extends  BaseContentDto implements BaseEntity {
          
	    private long cid;
	    private long topicId;
		private int isTop;
	    
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

            private int v_lv;

            private int level;

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
    public static class AcImageElement  implements BaseEntity {
          
		private long fid;
		private String fragmentImage;
		private String fragment;
		private int type;
		private int contentType;
		private String extra;
		private Date createTime;
		private long topicId;
		private String title;
		private long uid;
		private String nickName;
		private int v_lv;
		private int level;
		private int likeCount;
		private int isLike;
		private String avatar;
    }
}
