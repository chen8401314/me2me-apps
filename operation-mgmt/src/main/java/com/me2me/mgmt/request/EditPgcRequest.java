package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

public class EditPgcRequest {

	@Getter
    @Setter
	private long pgcId;
	
	@Getter
    @Setter
	private String txtTitle;
	
	@Getter
    @Setter
	private String txtTags;
	
	@Getter
    @Setter
	private String coverImgUrl;
	
	@Getter
    @Setter
	private String qiniuImg;
	
	@Getter
    @Setter
	private MultipartFile fuCoverImg;
	
	@Getter
    @Setter
	private String txtContent;
}
