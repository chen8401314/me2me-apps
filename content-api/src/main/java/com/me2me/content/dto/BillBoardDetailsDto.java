package com.me2me.content.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

import java.util.List;

/**
 * Created by pc329 on 2017/3/21.
 */
@Data
public class BillBoardDetailsDto implements BaseEntity {

    // 榜单ID
    private long listId;

    // 榜单名字
    private String title;

    // 榜单类型
    private int type;

    // 榜单描述
    private String summary;

    private String bgColor;

    private String coverImage;

    private int coverWidth;

    private int coverHeight;

    private int subType;


    private List<InnerDetailData> subList = Lists.newArrayList();

    @Data
    public static class InnerDetailData implements BaseEntity{

    	private long sinceId;
    	
        private int subType;

        private long uid;

        private String avatar;

        private String nickName;

        private int v_lv;

        private int level;
        
        private String avatarFrame;

        private int isFollowed;

        private int isFollowMe;

        private int contentType;

        private int favorite;

        private long id;

        private long cid;

        private long topicId;

        private long forwardCid;

        private String title;

        private String coverImage;

        private int internalStatus;

        private long favoriteCount;

        private long readCount;

        private long likeCount;

        private long reviewCount;

        private String introduced;
        
        private String tags;
        private Integer price;
        private double priceRMB;
    	
    	private Integer showRMBBrand;
    	
    	private Integer showPriceBrand;
    }





}
