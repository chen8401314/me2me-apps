package com.me2me.content.cache;

import lombok.Data;

/**
 * UGC 评论推送状态
 * 该结构采用String结构
 * @author pc340
 *
 */
@Data
public class UgcPushStatus {
	
	private static final String UGC_PUSH_STATUS_KEY = "ugc:push:status:";

	private String key;
    private String value;
    
    public UgcPushStatus(long cid, String value){
    	this.key = UGC_PUSH_STATUS_KEY + cid;
    	this.value = value;
    }
}
