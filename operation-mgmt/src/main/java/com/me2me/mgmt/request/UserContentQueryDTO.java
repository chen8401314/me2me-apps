package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.content.dto.ShowUserContentsDTO;

public class UserContentQueryDTO {

	@Getter
	@Setter
	private long uid;
	
	@Getter
	@Setter
	private ShowUserContentsDTO articleReviewDTO;
	
	@Getter
	@Setter
	private ShowUserContentsDTO ugcDTO;
	
	@Getter
	@Setter
	private ShowUserContentsDTO ugcReviewDTO;
	
	@Getter
	@Setter
	private ShowUserContentsDTO topicDTO;
	
	@Getter
	@Setter
	private ShowUserContentsDTO topicFragmentDTO;
}
