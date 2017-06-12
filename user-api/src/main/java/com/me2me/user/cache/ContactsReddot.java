package com.me2me.user.cache;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

/**
 * 通讯录红点缓存，采用Map结构
 * @author pc340
 *
 */
@Data
public class ContactsReddot implements BaseEntity {
	private static final long serialVersionUID = 3618624380454222507L;

	private String key = "user:contacts:reddot";
    private String field;
    private String value;
    
    public ContactsReddot(long uid, String value){
    	this.field = String.valueOf(uid);
    	this.value = value;
    }
}
