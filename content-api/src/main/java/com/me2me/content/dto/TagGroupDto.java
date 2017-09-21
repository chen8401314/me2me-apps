package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author pc339 on 2017/9/21
 *
 */
@Data
public class TagGroupDto implements BaseEntity {

    private List<KingdomHotTag> kingdomHotTagList = Lists.newArrayList();


    @Data
    public static class KingdomHotTag implements BaseEntity{
    	
    	private static final long serialVersionUID = -8781449680844112539L;

        private long tagId;

        private String tagName;

        private String coverImage;

        private int personCount;

        private int kingdomCount;

        private int isShowLikeButton;

        private List<ImageData> imageData = Lists.newArrayList();
    }

    @Data
    public static class ImageData implements BaseEntity{
    	
    	private static final long serialVersionUID = -8381449680844812539L;

        private String coverImage;

    }

}
