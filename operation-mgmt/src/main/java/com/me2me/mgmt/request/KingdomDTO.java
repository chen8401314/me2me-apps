package com.me2me.mgmt.request;

import lombok.Data;

@Data
public class KingdomDTO {

	private long topicId;
	private String alias;
	
	private String title;
	private long uid;
	private String nickName;
	private int price;
}
