package com.me2me.content.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

/**
 * Created by pc339 on 2017/9/22.
 */
@Data
public class UserLikeDto implements BaseEntity {

	private KingdomTag newKingdomTag;

	
    @Data
    public static class KingdomTag implements BaseEntity{
    	
    	private static final long serialVersionUID = -8781449680844112539L;

        private long tagId;

        private String tagName;

        private String coverImage;

        private int personCount;

        private int kingdomCount;
        
        private int isTop;

        private List<ImageData> imageData = Lists.newArrayList();
    }

    @Data
    public static class ImageData implements BaseEntity{
    	
    	private static final long serialVersionUID = -8381449680844812539L;

        private String coverImage;

    }
}
