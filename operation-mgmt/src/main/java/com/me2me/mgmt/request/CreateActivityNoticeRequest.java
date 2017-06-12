package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

public class CreateActivityNoticeRequest {

	@Getter
    @Setter
	private long activityId;
	@Getter
    @Setter
	private String txtTitle;
	@Getter
    @Setter
	private MultipartFile FUCoverImg;
	@Getter
    @Setter
	private String txtContent;
}
