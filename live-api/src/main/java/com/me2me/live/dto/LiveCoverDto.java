package com.me2me.live.dto;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/13
 * Time :19:22
 */
@Data
public class LiveCoverDto implements BaseEntity{
	private static final long serialVersionUID = 6369649571729593474L;

	private String title;

    private Date createTime;

    private long lastUpdateTime;

    private String coverImage;

    private long uid ;

    private String avatar;

    private String nickName;

    private int topicCount;

    private int reviewCount;

    // 阅读数（暂时未添加）
    private int readCount;

    // 成员数（暂时未添加）
    private int membersCount;

    private int internalStatus;

    //直播二维码
    private String liveWebUrl;

    private int v_lv;

    private int hasFavorite;
    
    //王国类型，0个人王国，1000聚合王国
    private int type;
    
    //子王国数
    private int acCount;
    //子王国top列表
    private List<TopicElement> acTopList = Lists.newArrayList();
    
    //被聚合次数，也即被聚合的聚合王国数
    private int ceCount;
    
    @Data
    public static class TopicElement implements BaseEntity{
		private static final long serialVersionUID = 1465887396904072679L;
    	
		private long topicId;
		private String title;
		private String coverImage;
		private int internalStatus;
    }
}
