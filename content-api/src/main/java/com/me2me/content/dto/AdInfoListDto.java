package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pc329 on 2017/3/17.
 */
@Data
public class AdInfoListDto implements BaseEntity {

    private List<AdInfoData> listData = Lists.newArrayList();


    @Data
    public static class AdInfoData implements BaseEntity{
    	
    	private static final long serialVersionUID = -8781449680844812539L;

        private long adid;

        private String adTitle;

        private String adCover;

        private int adCoverWidth;

        private int adCoverHeight;

        private int type;

        private String adUrl;

        private long topicId;

        private int topicType;

        private int topicInternalStatus;

    }


}
